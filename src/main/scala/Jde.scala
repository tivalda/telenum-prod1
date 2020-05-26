package ci.telenum
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import model.Model._
import java.sql.Date
import java.text.SimpleDateFormat

import config.ResConfig
import org.apache.spark.sql.hive.HiveContext

object Jde extends  App {
  override
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    //System.setProperty("hadoop.home.dir", "C:\\hadoop");
    //Création de mon context spark avec tous les coeurs disponible de la machine
    val sconf = new SparkConf().setAppName("Telenum-JDE")
    sconf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")
    sconf.set("es.index.auto.create", "true")
    val sc = new SparkContext(sconf)
    val sqlContext = new SQLContext(sc)
    val hiveContext = new HiveContext(sc)
    import sqlContext.implicits._

    var date = ""
    if (args.length == 0) {
      println("Mettez la date(YYYYMMDD)")
      sc.stop()
    }
    if (args.length >= 2) {
      println("Trop de paramètres")
      println("Mettez la date(YYYYMMDD)")
      sc.stop()
    }
    if (args.length == 1) {
      date = args(0)
    }

    val root_dir = ResConfig.Files.root_dir
    //val jde_data = root_dir + ResConfig.Files.jde_data
    val agces_jde_data = root_dir + ResConfig.Files.agence_jde

    //data_om
    val DATE_FORMAT = "dd/MM/yyyy"
    val date_format = new SimpleDateFormat(DATE_FORMAT)

    @transient
    val dfr = sqlContext.read.format("csv")
      .option("header", "true")
      .option("delimiter", ";")
      .option("multiLine", "true")
      .option("encoding", "UTF-8")

    //val jde_rdd = dfr.load(jde_data)

    println(ResConfig.Sql.jde.format(date))
    val jde_rdd = sqlContext.read.format("jdbc")
      .option("url", ResConfig.JdeDb.url)
      .option("driver", ResConfig.JdeDb.driver)
      .option("user", ResConfig.JdeDb.user)
      .option("password", ResConfig.JdeDb.password)
      .option("dbtable", ResConfig.Sql.jde.format(date))
      .load()

    jde_rdd.show(5)

    /*val jde_rdd_1 = jde_rdd
      .flatMap{f =>
        try {
          Some(
            JdeStruct(
              new Date(date_format.parse(f.getString(0)).getTime).formatted("%1$tY%1$tm%1$td"),
              "JDE",
              "JDE",
              "",
              f.getString(1),
              f.getString(2),
              f.getString(3),
              f.getString(4),
              f.getString(5),
              f.getString(6),
              f.getString(7).toDouble,
              f.getString(8).toDouble,
              f.getString(9),
              f.getString(10)
            )
          )
        }
         catch {
           case _: Throwable=> None
         }
      }.toDF()*/

    //jde_rdd_1.show(5)
    jde_rdd.registerTempTable("jde")

    /*******************/
    //Agences Bscs Psm
    val agces_jde_rdd = dfr.load(agces_jde_data)

    agces_jde_rdd.take(5).foreach(println)

    val agces_jde_rdd_1 = agces_jde_rdd.flatMap{
      f =>
          try {
            Some(
          Agences(f.getString(0).trim,
            f.getString(1).trim,
            f.getString(2).trim,
            f.getString(3).trim,
            f.getString(4).trim,
            f.getString(5).trim)
            )
      } catch {case _: Throwable=> None}
        }.toDF()

    agces_jde_rdd_1.registerTempTable("agence")

    /**********************************/

    val jde_final_df = sqlContext.sql(
      """select j.DATE_VENTE,
        |a.BASE_ORG as CANAL,
        |a.BASE_ORG as TYPE_CANAL,
        |a.GROUPEMENT,
        |j.CODE_ENTITE ,
        |j.LIBELLE_ENTITE,
        |j.MODE_ACCES ,
        |j.ACTION ,
        |j.CODE_EQUIPEMENT,
        |j.LIBELLE_EQUIPEMENT,
        |cast(j.MONTANT_TTC as bigint) as MONTANT_TTC,
        |cast(j.MONTANT_HT as bigint) as MONTANT_HT,
        |j.RESPONSABLE_VENTE,
        |j.CONTACT_CLIENT,
        |'' AS LONGITUDE,
        |'' AS LATITUDE,
        |'' AS UA,
        |'' AS DEPARTEMENT,
        |'' AS ZONE,
        |'' AS REGION,
        |'' AS DISTRICT,
        |a.PARTENAIRE AS ZONE_COMMERCIALE,
        |'' as REGION_OCI from jde j left join agence a on (trim(j.CODE_ENTITE) = trim(a.CODE_ENTITE))""".stripMargin)

  jde_final_df.show(5)

    /*ES index insertion*************************/
    println (configEs(ResConfig.ElasticsearchTest.es_nodes, ResConfig.ElasticsearchTest.es_port))
  jde_final_df.write.format("org.elasticsearch.spark.sql")
      .options(configEs(ResConfig.ElasticsearchTest.es_nodes, ResConfig.ElasticsearchTest.es_port))
      .mode("append")
      .save("telenum_transaction/data") //ResConfig.ElasticsearchTest.telenum_index

    /*Hive insertion*****************************/
    hiveContext.createDataFrame(jde_final_df.rdd, schema)
    hiveContext.cacheTable("tmp")
    hiveContext.sql("""insert into drone.telenum_data select * from tmp""")

    /*Clearing cache**************************/
    hiveContext.dropTempTable("tmp")
    hiveContext.clearCache()
    sqlContext.dropTempTable("jde")
    sqlContext.dropTempTable("agence")
    sqlContext.clearCache()
  }


}
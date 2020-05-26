package ci.telenum
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import model.Model._
import java.sql.Date
import java.text.SimpleDateFormat
import config.ResConfig

object Om extends  App {
  override
  def main(args: Array[String]): Unit = {
   Logger.getLogger("org").setLevel(Level.ERROR)
    System.setProperty("hadoop.home.dir", "C:\\hadoop");
    //Création de mon context spark avec tous les coeurs disponible de la machine
    val sconf = new SparkConf().setAppName("Telenum-OM")
    val sc = new SparkContext(sconf)
    val sqlContext = new SQLContext(sc)
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
    val om_data = root_dir + ResConfig.Files.om_data
    val agces_psm_data = root_dir + ResConfig.Files.agence_psm

    //data_om
    val DATE_FORMAT = "dd/MM/yyyy"
    val date_format = new SimpleDateFormat(DATE_FORMAT)

    @transient
    val dfr = sqlContext.read.format("csv")
      .option("header", "true")
      .option("delimiter", ";")
      .option("multiLine", "true")
      .option("encoding", "UTF-8")

    val om_rdd = dfr.load(om_data)

    om_rdd.take(5).foreach(println)

    println(ResConfig.Sql.om.format(date))

    val om_rdd_1 = om_rdd
      .flatMap{f =>
        try {
          Some(
            OmStruct(
              new Date(date_format.parse(f.getString(0)).getTime).formatted("%1$tY%1$tm%1$td"),
              f.getString(1),
              f.getString(2),
              f.getString(3),
              f.getString(4),
              f.getString(5),
              f.getString(6),
              f.getString(7),
              f.getString(8),
              f.getString(9),
              f.getString(10).toDouble,
              f.getString(11).toDouble,
              f.getString(12),
              f.getString(13)
            )
          )
        }
        catch {
          case _: Throwable=> None
        }
      }.toDF()

    om_rdd_1.registerTempTable("jde")

    /*******************/
    //Agences Bscs Psm
    val agces_psm_rdd = dfr.load(agces_psm_data)

    agces_psm_rdd.take(5).foreach(println)

    val agces_psm_rdd_1 = agces_psm_rdd.flatMap{
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

    agces_psm_rdd_1.registerTempTable("agence")

    /**********************************/

    val om_final_df = sqlContext.sql(
      """select j.DATE_TRANSACTION as DATE_VENTE,
        |j.CANAL,
        |j.TYPE_CANAL ,
        |j.GROUPEMENT,
        |j.CODE_ENTITE ,
        |j.LIBELLE_ENTITE,
        |j.MODE_ACCES ,
        |j.ACTION ,
        |j.CODE_EQUIPEMENT,
        |j.LIBELLE_EQUIPEMENT,
        |j.MONTANT_TTC,
        |j.MONTANT_HT,
        |j.RESPONSABLE_VENTE,
        |j.CONTACT_CLIENT,
        |'' as longitude,
        |'' as latitude,
        |'' as ua,
        |'' as departement,
        |'' as zone,
        |'' as region,
        |'' as district,
        |a.PARTENAIRE as zone_commerciale,
        |'' as region_oci from jde j left join agence a on (trim(j.CODE_ENTITE) = trim(a.CODE_ENTITE))""".stripMargin)

    om_final_df.show(5)

    /**************************/
    /* val index = "telenum_transaction/data"
     val configES = Map("pushdown" -> "true",
       "es.nodes" ->"10.242.68.50",
       "es.port" -> "9200",
       "es.query"-> "?q=*",
       "es.index.auto.create" -> "yes")

   jde_final_df.write.format("org.elasticsearch.spark.sql")
       .options(configES)
       .mode("append")
       .save(index)

     */
  }
}

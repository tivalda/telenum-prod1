package ci.telenum.model

import org.apache.spark.sql.types.{DoubleType, LongType, StringType, StructField, StructType}

object Model {
  def configEs(es_nodes:String, es_port:String) : Map[String, String] ={
    Map("pushdown" -> "true",
      "es.nodes" -> es_nodes,
      "es.port" -> es_port,
      "es.query" -> "?q=*",
      "es.index.auto.create" -> "yes")
  }
  val schema = StructType(
    StructField("DATE_VENTE", StringType, true) ::
      //StructField("HEURE", StringType, true) ::
      StructField("CANAL", StringType, true) ::
      StructField("TYPE_RESEAU", StringType, true) ::
      StructField("GROUPEMENT", StringType, true) ::
      StructField("CODE_ENTITE", StringType, true) ::
      StructField("LIBELLE_ENTITE", StringType, true) ::
      StructField("APPLICATION_MOBILE", StringType, true) ::
      StructField("ACTION", StringType, true) ::
      StructField("CODE_EQUIPEMENT", StringType, true) ::
      StructField("LIBELE_EQUIPEMENT", StringType, true) ::
      StructField("MONTANT_TTC", LongType, true) ::
      StructField("MONTANT_HT", LongType, true) ::
      StructField("RESPONSABLE_VENTE", StringType, true) ::
      StructField("CONTACT_CLIENT", StringType, true) ::
      StructField("LONGITUDE", StringType, true) ::
      StructField("LATITUDE", StringType, true) ::
      StructField("UA", StringType, true) ::
      StructField("DEPARTEMENT", StringType, true) ::
      StructField("ZONE", StringType, true) ::
      StructField("REGION", StringType, true) ::
      StructField("DISTRICT", StringType, true) ::
      StructField("ZONE_COMMERCIALE", StringType, true) ::
      StructField("REGION_OCI", StringType, true) :: Nil)

  case class OmGeo(
                    LONGITUDE:String,
                    LATITUDE:String,
                    UA:String,
                    DEPARTEMENT:String,
                    ZONE:String,
                    REGION:String,
                    DISTRICT:String,
                    ZONE_COMMERCIALE:String,
                    REGION_OCI:String)

  case class Agences(CODE_ENTITE: String,
                     LIBELLE_ENTITE: String,
                     TYPE: String,
                     BASE_ORG: String,
                     PARTENAIRE: String,
                     GROUPEMENT: String)

  case class JdeStruct(DATE_VENTE:String,
                       CANAL:String,
                       TYPE_CANAL:String,
                       GROUPEMENT:String,
                       CODE_ENTITE:String,
                       LIBELLE_ENTITE:String,
                       MODE_ACCES:String,
                       ACTION:String,
                       CODE_EQUIPEMENT:String,
                       LIBELLE_EQUIPEMENT:String,
                       MONTANT_TTC: Double,
                       MONTANT_HT: Double,
                       RESPONSABLE_VENTE:String,
                       CONTACT_CLIENT:String)

  case class Otel(DATE_VENTE:String,
                  CANAL:String,
                  TYPE_CANAL:String,
                  GROUPEMENT:String,
                  CODE_ENTITE:String,
                  LIBELLE_ENTITE:String,
                  MODE_ACCES:String,
                  ACTION:String,
                  CODE_EQUIPEMENT:String,
                  LIBELLE_EQUIPEMENT:String,
                  MONTANT_TTC: Double,
                  MONTANT_HT: Double,
                  RESPONSABLE_VENTE:String,
                  CONTACT_CLIENT:String)

  case class PsmStruct(DATE_VENTE:String,
                 CANAL:String,
                 TYPE_CANAL:String,
                 GROUPEMENT:String,
                 CODE_ENTITE:String,
                 LIBELLE_ENTITE:String,
                 MODE_ACCES:String,
                 ACTION:String,
                 CODE_EQUIPEMENT:String,
                 LIBELLE_EQUIPEMENT:String,
                 MONTANT_TTC: Double,
                 MONTANT_HT: Double,
                 RESPONSABLE_VENTE:String,
                 CONTACT_CLIENT:String)

  case class OmStruct(DATE_TRANSACTION:String,
                CANAL:String,
                TYPE_CANAL:String,
                GROUPEMENT:String,
                CODE_ENTITE:String,
                LIBELLE_ENTITE:String,
                MODE_ACCES:String,
                ACTION:String,
                CODE_EQUIPEMENT:String,
                LIBELLE_EQUIPEMENT:String,
                MONTANT_TTC:Double,
                MONTANT_HT:Double,
                RESPONSABLE_VENTE:String,
                CONTACT_CLIENT:String
                /*LONGITUDE:String,
                  LATITUDE:String,
                  UA:String,
                  DEPARTEMENT:String,
                  ZONE:String,
                  REGION:String,
                  DISTRICT:String,
                  ZONE_COMMERCIALE:String,
                  REGION_OCI:String*/)

  case class Pdvl(PARTENAIRE: String,
                  TYPE_PDV: String,
                  LOCALITE: String,
                  QUARTIER: String,
                  NUMERO_STK_B: String,
                  N_NOMAD: String,
                  LOGIN: String)
}

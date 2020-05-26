package ci.telenum.config


import com.typesafe.config.ConfigFactory

// java -Dconfig.file=<path>/custom.conf -jar Application.jar
object ResConfig {
  private val config = ConfigFactory.load()

  object ElasticsearchTest{
    private val conf = config.getConfig("elasticsearch_test")

    lazy val es_nodes = conf.getString("es_nodes")
    lazy val es_port = conf.getString("es_port")
    lazy val telenum_index = conf.getString("telenum_index")
  }

  object ElasticsearchProd{
    private val conf = config.getConfig("elasticsearch_prod")

    lazy val es_nodes = conf.getString("es_nodes")
    lazy val es_port = conf.getString("es_port")
    lazy val telenum_index = conf.getString("telenum_index")
  }

  object BaseOrangeTelenum{
    private val conf = config.getConfig("base_orange_telenum")

    lazy val sid = conf.getString("sid")
    lazy val ip = conf.getString("ip")
    lazy val login = conf.getString("login")
    lazy val password = conf.getString("password")
  }

  object Sql{
    private val conf = config.getConfig("sql")

    lazy val om = conf.getString("om")
    lazy val otel = conf.getString("otel")
    lazy val psm = conf.getString("bscs")
    lazy val jde: String = conf.getString("jde")
  }

  object Files{
    private val conf = config.getConfig("file")

    lazy val root_dir = conf.getString("root_dir")
    lazy val agence_psm = conf.getString("agence_psm")
    lazy val agence_jde = conf.getString("agence_jde")
    lazy val om_data = conf.getString("om_data")
    lazy val jde_data = conf.getString("jde_data")
  }


  object OtelDb{
    private val conf = config.getConfig("base_orange_telenum")

    lazy val sid: String = conf.getString("sid")
    lazy val ip: String = conf.getString("ip")
    lazy val login: String = conf.getString("login")
    lazy val password: String = conf.getString("password")
  }

  object JdeDb{
    private val conf = config.getConfig("base_oracle_jde")
    lazy val url = conf.getString("url")
    lazy val driver = conf.getString("driver")
    lazy val user = conf.getString("user")
    lazy val password = conf.getString("password")
  }

  object PsmDb{
    private val conf = config.getConfig("base_oracle_psm")
    lazy val url = conf.getString("url")
    lazy val driver = conf.getString("driver")
    lazy val user = conf.getString("user")
    lazy val password = conf.getString("password")
  }

  object DwhocitDb{
    private val conf = config.getConfig("base_oracle_dwhocit")
    lazy val url = conf.getString("url")
    lazy val driver = conf.getString("driver")
    lazy val user = conf.getString("user")
    lazy val password = conf.getString("password")
  }
}
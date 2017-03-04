package dao.index

import play.api.libs.json.JsObject

/**
  * Marco Ebert 18.02.17
  */
trait IndexDAO {

  /**
    * Unique name with timestamp.
    *
    * @return Unique name with timestamp.
    */
  def name: String

  /**
    * Search alias.
    *
    * @return Search alias.
    */
  def read: String

  /**
    * Index alias.
    *
    * @return Index alias.
    */
  def write: String

  /**
    * Backup alias.
    *
    * @return Backup alias.
    */
  def backup: Option[String]

  /**
    * Index settings.
    *
    * @return Index settings.
    */
  def settings: JsObject

  /**
    * Index mappings.
    *
    * @return Index mappings.
    */
  def mappings: Map[String, JsObject]

}

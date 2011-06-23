package templemore.jruby

import sbt._
import scala.Some
import sbt.OutputStrategy
import java.io.File

/**
 * @author Chris Turner
 */
case class Gem(name: String, version: Option[String], source: Option[String]) {

  override def toString = version match {
    case None => name
    case Some(v) => "%s-%s" format(name, v)
  }

  def toArgs = (name :: versionArgs) ++ sourceArgs

  private def versionArgs = version match {
    case Some(v) => "--version" :: v :: Nil
    case _ => Nil
  }

  private def sourceArgs = source match {
    case Some(s) => "--source" :: s :: Nil
    case _ => Nil
  }
}

case class GemInstaller(jRubyHome: File,
                        gemDir: File,
                        classpath: List[File],
                        outputStrategy: OutputStrategy) extends JRuby {
  gemDir.mkdirs()

  protected val javaOpts = List[String]()

  def installGem(gem: Gem, force: Boolean = false): Int = {
    def present(gem: Gem) = (gemDir / "gems" / gem.toString).exists

    if ( !present(gem) || force )
      jruby(("-S" :: "gem" :: "install" :: "--no-ri" :: "--no-rdoc" ::
            "--install-dir" :: gemDir.getPath :: Nil) ++ gem.toArgs)
    else 0
  }
}

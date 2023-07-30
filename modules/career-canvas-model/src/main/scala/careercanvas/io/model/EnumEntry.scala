package careercanvas.io.model

trait EnumEntry {

  override def toString: String = getClass.getSimpleName.stripSuffix("$")

}

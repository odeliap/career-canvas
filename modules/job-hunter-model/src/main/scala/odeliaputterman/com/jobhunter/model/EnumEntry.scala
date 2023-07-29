package odeliaputterman.com.jobhunter.model

trait EnumEntry {

  override def toString: String = getClass.getSimpleName.stripSuffix("$")

}

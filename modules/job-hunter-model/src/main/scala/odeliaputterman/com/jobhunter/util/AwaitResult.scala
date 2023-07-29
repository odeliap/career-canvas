package odeliaputterman.com.jobhunter.util

import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable}

/**
 * Trait to provide a utility for waiting the completion of an `Awaitable` and
 * retrieving its result.
 */
trait AwaitResult {

  // Specifies the time to wait before the result must be ready.
  val waitMins: FiniteDuration = 1.minute

  /**
   * Implicit class to add `waitForResult` method to any `Awaitable`.
   */
  implicit class Wait[T](val awaitObj: Awaitable[T]) {

    /**
     * Wait for the `Awaitable` to complete and retrieve its result.
     * It will throw an exception if the `Awaitable` is not completed within `waitTime`.
     */
    def waitForResult: T = Await.result(awaitObj, waitMins)
  }
}

import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

object FutureUtil {

  private final val DefaultConcurrentPoolSize      = 10
  private final val MaxConcurrentPoolSize          = 25
  private final val IncreasedMaxConcurrentPoolSize = 100
  private[this] val logger                         = LoggerFactory.getLogger(this.getClass)

  def join[A, B](futureA: Future[A], futureB: Future[B])(implicit ec: ExecutionContext): Future[(A, B)] =
    for {
      a <- futureA
      b <- futureB
    } yield (a, b)

  def join[A, B, C](futureA: Future[A], futureB: Future[B], futureC: Future[C])(implicit
    ec: ExecutionContext
  ): Future[(A, B, C)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
    } yield (a, b, c)

  def join[A, B, C, D](futureA: Future[A], futureB: Future[B], futureC: Future[C], futureD: Future[D])(implicit
    ec: ExecutionContext
  ): Future[(A, B, C, D)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
    } yield (a, b, c, d)

  def join[A, B, C, D, E](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
    } yield (a, b, c, d, e)

  def join[A, B, C, D, E, F](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
    } yield (a, b, c, d, e, f)

  def join[A, B, C, D, E, F, G](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
    } yield (a, b, c, d, e, f, g)

  def join[A, B, C, D, E, F, G, H](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G],
    futureH: Future[H]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G, H)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
      h <- futureH
    } yield (a, b, c, d, e, f, g, h)

  def join[A, B, C, D, E, F, G, H, I](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G],
    futureH: Future[H],
    futureI: Future[I]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G, H, I)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
      h <- futureH
      i <- futureI
    } yield (a, b, c, d, e, f, g, h, i)

  def join[A, B, C, D, E, F, G, H, I, J](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G],
    futureH: Future[H],
    futureI: Future[I],
    futureJ: Future[J]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G, H, I, J)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
      h <- futureH
      i <- futureI
      j <- futureJ
    } yield (a, b, c, d, e, f, g, h, i, j)

  def join[A, B, C, D, E, F, G, H, I, J, K](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G],
    futureH: Future[H],
    futureI: Future[I],
    futureJ: Future[J],
    futureK: Future[K]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G, H, I, J, K)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
      h <- futureH
      i <- futureI
      j <- futureJ
      k <- futureK
    } yield (a, b, c, d, e, f, g, h, i, j, k)

  def join[A, B, C, D, E, F, G, H, I, J, K, L](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G],
    futureH: Future[H],
    futureI: Future[I],
    futureJ: Future[J],
    futureK: Future[K],
    futureL: Future[L]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G, H, I, J, K, L)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
      h <- futureH
      i <- futureI
      j <- futureJ
      k <- futureK
      l <- futureL
    } yield (a, b, c, d, e, f, g, h, i, j, k, l)

  def join[A, B, C, D, E, F, G, H, I, J, K, L, M](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G],
    futureH: Future[H],
    futureI: Future[I],
    futureJ: Future[J],
    futureK: Future[K],
    futureL: Future[L],
    futureM: Future[M]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G, H, I, J, K, L, M)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
      h <- futureH
      i <- futureI
      j <- futureJ
      k <- futureK
      l <- futureL
      m <- futureM
    } yield (a, b, c, d, e, f, g, h, i, j, k, l, m)

  def join[A, B, C, D, E, F, G, H, I, J, K, L, M, N](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G],
    futureH: Future[H],
    futureI: Future[I],
    futureJ: Future[J],
    futureK: Future[K],
    futureL: Future[L],
    futureM: Future[M],
    futureN: Future[N]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G, H, I, J, K, L, M, N)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
      h <- futureH
      i <- futureI
      j <- futureJ
      k <- futureK
      l <- futureL
      m <- futureM
      n <- futureN
    } yield (a, b, c, d, e, f, g, h, i, j, k, l, m, n)

  def join[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G],
    futureH: Future[H],
    futureI: Future[I],
    futureJ: Future[J],
    futureK: Future[K],
    futureL: Future[L],
    futureM: Future[M],
    futureN: Future[N],
    futureO: Future[O]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
      h <- futureH
      i <- futureI
      j <- futureJ
      k <- futureK
      l <- futureL
      m <- futureM
      n <- futureN
      o <- futureO
    } yield (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)

  def join[A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P](
    futureA: Future[A],
    futureB: Future[B],
    futureC: Future[C],
    futureD: Future[D],
    futureE: Future[E],
    futureF: Future[F],
    futureG: Future[G],
    futureH: Future[H],
    futureI: Future[I],
    futureJ: Future[J],
    futureK: Future[K],
    futureL: Future[L],
    futureM: Future[M],
    futureN: Future[N],
    futureO: Future[O],
    futureP: Future[P]
  )(implicit ec: ExecutionContext): Future[(A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P)] =
    for {
      a <- futureA
      b <- futureB
      c <- futureC
      d <- futureD
      e <- futureE
      f <- futureF
      g <- futureG
      h <- futureH
      i <- futureI
      j <- futureJ
      k <- futureK
      l <- futureL
      m <- futureM
      n <- futureN
      o <- futureO
      p <- futureP
    } yield (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)

  def groupedSequence[T, R](originSeq: Iterable[T], concurrentPoolSize: Int = DefaultConcurrentPoolSize)(
    futureCreator: T => Future[R]
  )(implicit ec: ExecutionContext): Future[Seq[Try[R]]] = {
    if (concurrentPoolSize > MaxConcurrentPoolSize || concurrentPoolSize <= 0) {
      throw new IllegalArgumentException(
        s"Cannot use concurrent_pool_size of $concurrentPoolSize. " +
          s"Pool size should be between 0 - $MaxConcurrentPoolSize."
      )
    }
    doSequential[Iterable[T], Iterable[Try[R]]](originSeq.grouped(concurrentPoolSize).toIterable) { smallerSeq =>
      doConcurrent[T, R](smallerSeq)(futureCreator)
    }.map { responses =>
      responses.flatMap {
        case Success(value) =>
          value
        case Failure(ex) =>
          logger.error("Failed to do grouped sequence", ex)
          throw ex
      }
    }
  }

  def groupedSequenceWithIncreasedPoolSize[T, R](
    originSeq: Iterable[T],
    concurrentPoolSize: Int = DefaultConcurrentPoolSize
  )(futureCreator: T => Future[R])(implicit ec: ExecutionContext): Future[Seq[Try[R]]] = {
    if (concurrentPoolSize > IncreasedMaxConcurrentPoolSize || concurrentPoolSize <= 0) {
      throw new IllegalArgumentException(
        s"Cannot use concurrent_pool_size of $concurrentPoolSize. " +
          s"Pool size should be between 0 - $IncreasedMaxConcurrentPoolSize."
      )
    }
    doSequential[Iterable[T], Iterable[Try[R]]](originSeq.grouped(concurrentPoolSize).toIterable) { smallerSeq =>
      doConcurrent[T, R](smallerSeq)(futureCreator)
    }.map { responses =>
      responses.flatMap {
        case Success(value) =>
          value
        case Failure(ex) =>
          logger.error("Failed to do grouped sequence", ex)
          throw ex
      }
    }
  }

  def groupedSequenceWithErrorsIgnoredAndIncreasedPoolSize[T, R](
    originSeq: Iterable[T],
    concurrentPoolSize: Int = DefaultConcurrentPoolSize
  )(futureCreator: T => Future[R])(implicit ec: ExecutionContext): Future[Seq[R]] =
    groupedSequenceWithIncreasedPoolSize(originSeq, concurrentPoolSize)(futureCreator).map(_.flatMap(_.toOption))

  def groupedSequenceWithErrorsIgnored[T, R](
    originSeq: Iterable[T],
    concurrentPoolSize: Int = DefaultConcurrentPoolSize
  )(futureCreator: T => Future[R])(implicit ec: ExecutionContext): Future[Seq[R]] =
    groupedSequence(originSeq, concurrentPoolSize)(futureCreator).map(_.flatMap(_.toOption))

  def groupedSequenceSelfTransformation[T](originSeq: Iterable[T], concurrentPoolSize: Int = DefaultConcurrentPoolSize)(
    futureCreator: T => Future[T]
  )(implicit ec: ExecutionContext): Future[Seq[T]] =
    groupedSequence(originSeq, concurrentPoolSize)(futureCreator).map { results =>
      originSeq
        .zip(results)
        .map {
          case (_, Success(value)) =>
            value
          case (t, _) =>
            t
        }
        .toSeq
    }

  def doSequentialWithErrorsIgnored[T, R](originSeq: Iterable[T])(futureCreator: T => Future[R])(implicit
    ec: ExecutionContext
  ): Future[Seq[R]] = doSequential(originSeq)(futureCreator).map(_.flatMap(_.toOption))

  def doSequential[T, R](
    originSeq: Iterable[T]
  )(futureCreator: T => Future[R])(implicit ec: ExecutionContext): Future[Seq[Try[R]]] =
    originSeq.headOption match {
      case Some(t) =>
        Future
          .fromTry(Try(futureCreator(t)))
          .flatten
          .map(resp => Success(resp))
          .recover { case NonFatal(ex) =>
            logger.error("Exception thrown", ex)
            Failure(ex)
          }
          .flatMap { headResp =>
            doSequential[T, R](originSeq.tail)(futureCreator).map { tailResponses =>
              Seq(headResp) ++ tailResponses
            }
          }
      case None =>
        Future.successful(Seq.empty)
    }

  def doConcurrent[T, R](
    originSeq: Iterable[T]
  )(futureCreator: T => Future[R])(implicit ec: ExecutionContext): Future[Seq[Try[R]]] =
    Future
      .sequence(
        originSeq.map { s =>
          Future
            .fromTry(Try(futureCreator(s)))
            .flatten
            .map(resp => Success(resp))
            .recover { case NonFatal(ex) =>
              Failure(ex)
            }
        }
      )
      .map(_.toSeq)
}

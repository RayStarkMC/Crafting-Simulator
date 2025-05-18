package net.raystarkmc.craftingsimulator.lib.cats

import cats.*
import cats.data.*

type ApplicativeErrorWith[E] = [F[_]] =>> ApplicativeError[F, E]
type ApplicativeErrorWithNec[E] = ApplicativeErrorWith[NonEmptyChain[E]]

type MonadErrorWith[E] = [F[_]] =>> MonadError[F, E]
type MonadErrorWithNec[E] = MonadErrorWith[NonEmptyChain[E]]

private type EitherWith[E] = [A] =>> Either[E, A]
type EitherWithNec[E] = EitherWith[NonEmptyChain[E]]

type EitherNecT[F[_], E, A] = EitherT[F, NonEmptyChain[E], A]
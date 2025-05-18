package net.raystarkmc.craftingsimulator.lib.cats

import cats.*
import cats.data.*

type ApplicativeErrorWith[E] = [F[_]] =>> ApplicativeError[F, E]
type ApplicativeErrorWithNec[E] = ApplicativeErrorWith[NonEmptyChain[E]]

type MonadErrorWith[E] = [F[_]] =>> MonadError[F, E]
type MonadErrorWithNec[E] = MonadErrorWith[NonEmptyChain[E]]
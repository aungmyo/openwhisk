/*
 * Copyright 2015-2016 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package common

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import scala.concurrent.duration.DurationInt

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Suite

/** Helper trait to provide an implicit actor system and execution context
 *  to tests, and properly shut it down at the end.
 *
 *  If you use this trait and override afterAll(), make sure to also call super.afterAll().
 */
trait WskActorSystem extends BeforeAndAfterAll {
    self: Suite =>

    implicit val actorSystem: ActorSystem = ActorSystem()

    implicit def executionContext: ExecutionContext = actorSystem.dispatcher

    override def afterAll() {
        super.afterAll()
        try {
            Await.result(Http().shutdownAllConnectionPools(), 30.seconds)
        } finally {
            actorSystem.terminate()
            Await.result(actorSystem.whenTerminated, 30.seconds)
        }
    }
}

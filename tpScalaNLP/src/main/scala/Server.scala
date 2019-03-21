import scala.xml.XML
import better.files.{File => ScalaFile, _}
import cats.effect.{Effect, IO}
import fs2.StreamApp
import io.circe._
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.{EntityDecoder, EntityEncoder, HttpService}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import sys.process._

import scala.concurrent.ExecutionContext
import scala.language.higherKinds
object Server extends StreamApp[IO] {

  import scala.concurrent.ExecutionContext.Implicits.global

  override def stream (args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = ServerStream.stream[IO]
}

object ServerStream {

  def textService[F[_] : Effect]: HttpService[F] = new TextService[F].service\nlp
  def graphService[F[_] : Effect]: HttpService[F] = new GraphService[F].service
  def operationService[F[_] : Effect]: HttpService[F] = new OperationService[F].service

  def stream[F[_] : Effect] (implicit executionContext: ExecutionContext): fs2.Stream[F, StreamApp.ExitCode] = {
    BlazeBuilder[F]
      .bindHttp(9000, "localhost")
      .mountService(textService, "/text")
      .mountService(graphService,"/graph")
      .mountService(operationService,"/nlp")
      .serve
  }
}

class TextService[F[_] : Effect] extends Http4sDsl[F] {
  implicit def circeJsonDecoder[A] (implicit decoder: Decoder[A]) = jsonOf[F,A]
  implicit def circeJsonEncoder[A] (implicit encoder: Encoder[A]) = jsonEncoderOf[F, A]

  case class Message (message: String)


  val service: HttpService[F] = HttpService[F] {
      case GET -> Root =>
        Ok(TextEventSourcing.textList)
      case GET -> Root / LongVar(id) =>
        TextEventSourcing.textLookup(id).map(Ok(_)).getOrElse(NotFound())
      case req@POST -> Root =>
        case class TextCommand (titre:String, contenu : String)
        req.decode[TextCommand] {
          p => {
            print(p)
            TextEventSourcing.textCreate(p.titre,p.contenu)
            NoContent()
          }
        }
      case req@PUT -> Root / LongVar(id) / "changeTitre" =>
        case class NewLabel (titre: String,contenu:String)
        req.decode[NewLabel] {
          p =>
            TextEventSourcing.textRename(id, p.titre,p.contenu).map(Ok(_)).getOrElse(NotFound())
        }
  }
}


class GraphService[F[_] : Effect] extends Http4sDsl[F] {
  implicit def circeJsonDecoder[A] (implicit decoder: Decoder[A]) = jsonOf[F,A]
  implicit def circeJsonEncoder[A] (implicit encoder: Encoder[A]) = jsonEncoderOf[F, A]
  case class Message (message: String)
  val service: HttpService[F] = HttpService[F] {
    case GET -> Root =>
      Ok(GraphEventSourcing.graphList)
    case GET -> Root / LongVar(id) =>
      GraphEventSourcing.graphLookup(id).map(Ok(_)).getOrElse(NotFound())
    case req@POST -> Root =>
      case class GraphCommand (titre:String, contenu : String)
      req.decode[GraphCommand] {
        p => {
          print(p)
          GraphEventSourcing.graphCreate(p.titre,p.contenu)
          NoContent()
        }
      }
    case req@PUT -> Root / LongVar(id) / "changeTitre" =>
      case class NewTitre (titre: String,contenu:String)
      req.decode[NewTitre] {
        p =>
          GraphEventSourcing.graphRename(id, p.titre,p.contenu).map(Ok(_)).getOrElse(NotFound())
      }
  }
}













class OperationService[F[_] : Effect] extends Http4sDsl[F] {
  def commandLine(graph : String, text :String,nomFichier : String):Any = {

    s"Unitex-GramLab-3.1/App/UnitexToolLogger Normalize $text.txt -rworkspace/Unitex-GramLab/Unitex/French/Norm.txt --output_offsets=${text}_snt/normalize.out.offsets -qutf8-no-bom".!
    println("2")
    s"Unitex-GramLab-3.1/App/UnitexToolLogger Tokenize $text.snt -aworkspace/Unitex-GramLab/Unitex/French/Alphabet.txt --input_offsets=${text}_snt/normalize.out.offsets --output_offsets=${text}_snt/tokenize.out.offsets -qutf8-no-bom".!
    println("3")
    s"Unitex-GramLab-3.1/App/UnitexToolLogger Dico -t$text.snt -aworkspace/Unitex-GramLab/Unitex/French/Alphabet.txt Unitex-GramLab-3.1/French/Dela/dela-fr-public.bin Unitex-GramLab-3.1/French/Dela/ajouts$nomFichier.bin Unitex-GramLab-3.1/French/Dela/motsGramf-.bin -qutf8-no-bom".!
    println("4")
    s"Unitex-GramLab-3.1/App/UnitexToolLogger SortTxt ${text}_snt/dlf -l${text}_snt/dlf.n -oworkspace/Unitex-GramLab/Unitex/French/Alphabet_sort.txt -qutf8-no-bom".!
    println("5")
    s"Unitex-GramLab-3.1/App/UnitexToolLogger SortTxt ${text}_snt/dlc -l${text}_snt/dlc.n -oworkspace/Unitex-GramLab/Unitex/French/Alphabet_sort.txt -qutf8-no-bom".!
    println("6")
    s"Unitex-GramLab-3.1/App/UnitexToolLogger SortTxt ${text}_snt/err -l${text}_snt/err.n -oworkspace/Unitex-GramLab/Unitex/French/Alphabet_sort.txt -qutf8-no-bom".!
    println("7")
    s"Unitex-GramLab-3.1/App/UnitexToolLogger SortTxt ${text}_snt/tags_err -l${text}_snt/tags_err.n -oworkspace/Unitex-GramLab/Unitex/French/Alphabet_sort.txt -qutf8-no-bom".!
    println("8")
    s"Unitex-GramLab-3.1/App/UnitexToolLogger Grf2Fst2 Projet/$graph.grf -y --alphabet=workspace/Unitex-GramLab/Unitex/French/Alphabet.txt --debug -qutf8-no-bom".!
    println("9")
    s"Unitex-GramLab-3.1/App/UnitexToolLogger Locate -t$text.snt Projet/$graph.fst2 -aworkspace/Unitex-GramLab/Unitex/French/Alphabet.txt -A -M --all -b -Y --stack_max=1000 --max_matches_per_subgraph=200 --max_matches_at_token_pos=400 --max_errors=50 -qutf8-no-bom".!
    println("10")
    s"Unitex-GramLab-3.1/App/UnitexToolLogger Concord ${text}_snt/concord.ind --xml  -aworkspace/Unitex-GramLab/Unitex/French/Alphabet_sort.txt --CL -qutf8-no-bom".!


  }
  implicit def circeJsonDecoder[A](implicit decoder: Decoder[A]) = jsonOf[F, A]

  implicit def circeJsonEncoder[A](implicit encoder: Encoder[A]) = jsonEncoderOf[F, A]

  case class Message(message: String)

  val service: HttpService[F] = HttpService[F] {
    case GET -> Root / LongVar(idText) / LongVar(idGraph) / "getESN" => {
      val g = GraphEventSourcing.graphLookup(idGraph)
      val t = TextEventSourcing.textLookup(idGraph)
      (g, t) match {
        case (Some(gr), Some(te)) => {
          val ftexte = ScalaFile(s"./workspace/Unitex-GramLab/Unitex/French/Corpus/${te.titre}.txt")
          ftexte.overwrite(te.contenu)
          val fgraph = ScalaFile(s"./Projet/${gr.titre}.grf")
          fgraph.overwrite(gr.contenu)
          s"./removeFormat.sh ./Projet/${gr.titre}.grf".!
          s"mkdir ./workspace/Unitex-GramLab/Unitex/French/Corpus/${te.titre}_snt".!
          commandLine(gr.titre, s"./workspace/Unitex-GramLab/Unitex/French/Corpus/${te.titre}",te.titre)
          val xml = XML.loadFile(s"./workspace/Unitex-GramLab/Unitex/French/Corpus/${te.titre}_snt/concord.xml")
          val res = xml \ "concordance" \ "ESN"
          Ok(res.map(x => x.text))
        }
        case _ => NotFound()
      }
    }
  }
}








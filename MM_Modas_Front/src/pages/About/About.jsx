// =========================================================
// About — "Nossa história" (/sobre) — MM GAMES
// =========================================================
import { Link } from 'react-router-dom'
import Mascot from '../../components/Logo/Mascot.jsx'
import './About.css'

const story = [
  {
    title: 'A conversa',
    text: 'Nossa loja nasceu de uma conversa entre amigos na faculdade. Entre uma aula e outra, as sessões de RPG e tabuleiro começaram a virar um projeto de verdade.',
  },
  {
    title: 'A criatividade',
    text: 'Foi quando Daniel revelou que sempre teve vontade de exercer sua criatividade com jogos: unir estratégia, narrativa e atitude ao redor da mesa.',
  },
  {
    title: 'O empreendedorismo',
    text: 'Essa paixão se juntou com o ímpeto empreendedor dos demais amigos — e assim nasceu a MM GAMES.',
  },
]

const values = [
  { title: 'Sem filtro', text: 'A gente fala de jogador pra jogador. Preço justo, curadoria sincera e zero enrolação.' },
  { title: 'Cuidado de colecionador', text: 'Caixas embaladas com proteção reforçada pra chegarem intactas na sua estante.' },
  { title: 'Diversão primeiro', text: 'Dos party games rápidos às campanhas épicas de RPG que duram anos.' },
]

function About() {
  return (
    <div className="about">
      <section className="container about__hero">
        <div>
          <span className="eyebrow">Nossa história</span>
          <h1>
            Todo jogador tem um lado <span className="about__accent">Menino Mau</span>.
          </h1>
          <p className="about__lead">
            Aquele blefe na última rodada ou a jogada que vira a partida de cabeça pra baixo. A MM GAMES é uma loja
            feita por amigos apaixonados por jogos de mesa, RPG e boas histórias.
          </p>
        </div>
        <div className="about__portrait" aria-hidden="true">
          <Mascot size={260} title="" />
          <span className="about__caption">o Menino Mau, mestre oficial da taverna</span>
        </div>
      </section>

      <section className="container section">
        <h2 className="section-title">Como tudo começou</h2>
        <ol className="timeline">
          {story.map((item, i) => (
            <li key={item.title} className="timeline__item">
              <span className="timeline__dot">{i + 1}</span>
              <div>
                <h3>{item.title}</h3>
                <p>{item.text}</p>
              </div>
            </li>
          ))}
        </ol>
      </section>

      <section className="values">
        <div className="container">
          <h2 className="section-title">No que a gente acredita</h2>
          <div className="values__grid">
            {values.map((v) => (
              <div key={v.title} className="value">
                <h3>{v.title}</h3>
                <p>{v.text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="container about__cta">
        <h2>Pronto pra começar sua próxima partida?</h2>
        <Link to="/loja" className="btn btn--primary">
          Explorar o catálogo
        </Link>
      </section>
    </div>
  )
}

export default About

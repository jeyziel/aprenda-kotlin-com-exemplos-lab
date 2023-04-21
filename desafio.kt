enum class Nivel { BASICO, INTERMEDIARIO, DIFICIL }
enum class Sexo { HOMEM, MULHER, OUTROS }
enum class Status { CONCLUIDO, PENDENTE}


class FormacaoExpirada(message: String): Throwable(message)

data class Usuario(val nome: String, val sexo: Sexo){
    var formacoes = HashMap<Formacao, Boolean>()


    fun concluirFormacao(formacao: Formacao){

        if (formacoes.containsKey(formacao)){
            formacoes.put(formacao, true)
        }

    }


    fun getFormacoes(status : Status): List<Formacao>{

        return when(status){
            Status.CONCLUIDO -> formacoes.filterValues { it == true }.keys.toList()
            Status.PENDENTE -> formacoes.filterValues { it == false }.keys.toList()
        }

    }

    fun resumo() : String {
        return """
            Nome: ${nome}
            Sexo: ${sexo}
            Formações Concluídas: ${getFormacoes(Status.CONCLUIDO).map { it.nome }.joinToString(", ") }
            Formações Pendentes: ${getFormacoes(Status.PENDENTE).map { it.nome }.joinToString(", ") }
        """
    }



}

data class ConteudoEducacional(
    var nome: String,
    val duracao: Int = 60
)

data class Formacao(
    val nome: String,
    var conteudos: MutableSet<ConteudoEducacional>,
    val nivel: Nivel,
    val isOpen: Boolean = true
) {

    val inscritos = mutableSetOf<Usuario>()

    @Throws
    fun matricular(usuario: Usuario) {


        if (!isOpen) throw FormacaoExpirada("A formação não se encontra mais aberta")

        inscritos.add(usuario)
        usuario.formacoes.set(this, false)
    }

    fun duracaoFormacao() : Int{
        return conteudos.sumOf { it.duracao }
    }

    fun proporcaoConclusao() : Double{

        var countConcluidos = 0;

        inscritos.forEach {
            if (it.getFormacoes(Status.CONCLUIDO).contains(this)) {
                countConcluidos++
            }
        }


        return try {
            val division: Double =  countConcluidos.div(inscritos.size.toDouble()) * 100
            return division
        } catch (e: ArithmeticException){
            0.0
        }



    }

     fun resumo(): String {
        return """
            Nome: ${nome}
            Duração da formação: ${duracaoFormacao()} minutos
            Quantidade de Usuários Inscritos: ${inscritos.size}
            Proporção de conclusão da formação: ${proporcaoConclusao()} %
          """
    }
}


fun main() {

    var maria = Usuario("Maria", Sexo.MULHER)
    val joao = Usuario("João", Sexo.HOMEM)
    val jeyziel = Usuario("Jeyziel", Sexo.HOMEM)

    // Criando Conteudos Educaionais
    val introducaoDIO = ConteudoEducacional("Introdução à DIO", 1)

    val kotlinBasico = ConteudoEducacional("Introdução ao Kotlin", 60)
    val KotlinFuncoes = ConteudoEducacional("Funções em Kotlin", 120)
    val KotlinClasses = ConteudoEducacional("Classes em Kotlin", 180)

    val javaBasico = ConteudoEducacional("Introdução ao JAVA", 60)
    val javaFuncoes = ConteudoEducacional("Funções com java", 160)
    val javaOO = ConteudoEducacional("Orientacao a objetos com java", 180)


    // Conjunto de conteúdos
    val conteudosKotlin = mutableSetOf(introducaoDIO, kotlinBasico, KotlinFuncoes, KotlinClasses)
    val conteudosJava = mutableSetOf(introducaoDIO, javaBasico, javaFuncoes, javaOO)


    // Formacao JAVA E KOLTIN
    val formacaoKotlin = Formacao("Kotlin", conteudosKotlin, Nivel.BASICO )
    formacaoKotlin.matricular(maria)
    formacaoKotlin.matricular(joao)


    val formacaoJava = Formacao("Java", conteudosJava, Nivel.BASICO )
    formacaoJava.matricular(maria)

    /**
     * Conclusão de Formação
     */
    maria.concluirFormacao(formacaoJava)
    maria.concluirFormacao(formacaoKotlin)

   // joao.concluirFormacao(formacaoKotlin)

    /**
     * Resumo dos onde será retornando seu nome, quantidade de formações concluídas e pendentes
     */

    println("RESUMO DOS INCRITOS: ")
    println(maria.resumo())
    println(joao.resumo())

    /**
     * Resumo das formações Java e Kotlin, onde será retornado a duração da formamação, quantos pessoas estão incritas
     * e quantas já concluíram o curso
     */
    println("RESUMO DAS FORMAÇÕES: ")
    println(formacaoJava.resumo())
    println(formacaoKotlin.resumo())












}
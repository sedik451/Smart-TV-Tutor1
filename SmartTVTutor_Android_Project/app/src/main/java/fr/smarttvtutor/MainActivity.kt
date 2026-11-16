package fr.smarttvtutor

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.*
import android.graphics.Typeface
import android.graphics.Color
import android.view.ViewGroup
import kotlin.random.Random

data class QuizQuestion(
    val level: String, val subject: String, val question: String,
    val answers: List<String>, val correct: Int, val explanation: String
)

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private var timer: CountDownTimer? = null
    private var remainingMs = 10 * 60 * 1000L
    private var attempts = 0
    private var current: QuizQuestion? = null
    private var childAge = 7
    private var language = "fr"
    private var watching = false
    private lateinit var status: TextView

    private val questions = listOf(
        QuizQuestion("CP", "Mathématiques", "Combien font 3 + 4 ?", listOf("6", "7", "8"), 1, "3 + 4 = 7."),
        QuizQuestion("CP", "Français", "Quel mot commence par le son « ch » ?", listOf("chat", "pomme", "vélo"), 0, "« Chat » commence par le son ch."),
        QuizQuestion("CE1", "Mathématiques", "Combien font 5 × 3 ?", listOf("8", "15", "20"), 1, "5 groupes de 3 font 15."),
        QuizQuestion("CE2", "Sciences", "Quel organe permet de respirer ?", listOf("Le cœur", "Les poumons", "L'estomac"), 1, "Les poumons permettent de respirer."),
        QuizQuestion("CM1", "Géographie", "Quelle est la capitale de la France ?", listOf("Lyon", "Paris", "Marseille"), 1, "Paris est la capitale de la France."),
        QuizQuestion("CM2", "Sciences", "À quelle température l'eau bout-elle au niveau de la mer ?", listOf("50 °C", "100 °C", "150 °C"), 1, "Au niveau de la mer, l'eau bout vers 100 °C."),
        QuizQuestion("6e", "Mathématiques", "Combien vaut 1/2 + 1/4 ?", listOf("2/6", "3/4", "1/6"), 1, "1/2 = 2/4, donc 2/4 + 1/4 = 3/4."),
        QuizQuestion("5e", "Sciences", "Quelle planète est surnommée la planète rouge ?", listOf("Mars", "Vénus", "Jupiter"), 0, "Mars paraît rouge à cause des oxydes de fer à sa surface.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showHome()
    }

    private fun base(title: String) {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 28, 48, 28)
            setBackgroundColor(Color.rgb(18, 28, 45))
        }
        val heading = TextView(this).apply {
            text = title; textSize = 30f; setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
            setPadding(8, 8, 8, 24)
        }
        root.addView(heading, match())
        setContentView(root)
    }

    private fun match() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private fun label(text: String, size: Float = 20f): TextView =
        TextView(this).apply {
            this.text = text; textSize = size; setTextColor(Color.WHITE)
            gravity = Gravity.CENTER; setPadding(8, 12, 8, 12)
        }

    private fun button(text: String, action: () -> Unit): Button =
        Button(this).apply {
            this.text = text; textSize = 18f; isFocusable = true
            setOnClickListener { action() }
            root.addView(this, LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(8, 7, 8, 7) })
        }

    private fun showHome() {
        timer?.cancel(); watching = false
        base("Smart TV Tutor")
        root.addView(label("Apprendre pour gagner 10 minutes de plus !", 21f), match())
        root.addView(label("Profil enfant • Âge : $childAge ans • Programme français"), match())
        button("Choisir l'âge / Choose age") { showAge() }
        button("Commencer / Start") { startWatching() }
        button("Parents / Parents") { showParents() }
        root.addView(label("Prototype 0.1 • Le minuteur fonctionne dans cette application uniquement.", 14f), match())
        root.getChildAt(1).requestFocus()
    }

    private fun showAge() {
        base("Âge de l'enfant / عمر الطفل")
        (3..12).forEach { age -> button("$age ans") { childAge = age; showHome() } }
        button("Retour") { showHome() }
    }

    private fun startWatching() {
        watching = true
        remainingMs = 10 * 60 * 1000L
        showWatch()
        runTimer()
    }

    private fun showWatch() {
        base("Temps de visionnage / وقت المشاهدة")
        status = label("10:00", 48f)
        root.addView(status, match())
        root.addView(label("Le temps est simulé ici : lancez votre contenu dans cette application de test.", 18f), match())
        button("Déclencher une question (test)") { timer?.cancel(); showQuestion() }
        button("Terminer") { showHome() }
    }

    private fun runTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(remainingMs, 1000) {
            override fun onTick(ms: Long) {
                remainingMs = ms
                val minutes = (ms / 60000).toInt()
                val seconds = ((ms % 60000) / 1000).toInt()
                status.text = "%02d:%02d".format(minutes, seconds)
            }
            override fun onFinish() { remainingMs = 0; showQuestion() }
        }.start()
    }

    private fun chooseQuestion(): QuizQuestion {
        val pool = when {
            childAge <= 5 -> questions.filter { it.level == "CP" }
            childAge <= 6 -> questions.filter { it.level in listOf("CP", "CE1") }
            childAge <= 8 -> questions.filter { it.level in listOf("CP", "CE1", "CE2") }
            childAge <= 10 -> questions.filter { it.level in listOf("CE2", "CM1", "CM2") }
            else -> questions.filter { it.level in listOf("CM2", "6e", "5e") }
        }
        return pool.random()
    }

    private fun showQuestion(message: String = "Choisis la bonne réponse !") {
        current = chooseQuestion()
        attempts = 0
        renderQuestion(message)
    }

    private fun renderQuestion(message: String) {
        val q = current ?: return
        base("Pause pédagogique • ${q.subject}")
        root.addView(label(q.question, 27f), match())
        root.addView(label(message, 18f), match())
        q.answers.forEachIndexed { index, answer ->
            button("${index + 1}. $answer") { checkAnswer(index) }
        }
    }

    private fun checkAnswer(index: Int) {
        val q = current ?: return
        if (index == q.correct) {
            base("Bravo ! 🎉")
            root.addView(label("Bonne réponse ! 10 minutes supplémentaires.", 25f), match())
            button("Continuer") { remainingMs = 10 * 60 * 1000L; showWatch(); runTimer() }
        } else {
            attempts++
            if (attempts == 1) {
                renderQuestion("Pas tout à fait. Essaie encore ! / حاول مرة أخرى")
            } else {
                base("On apprend ensemble !")
                root.addView(label(q.explanation, 22f), match())
                button("Question suivante") { showQuestion("Nouvelle question !") }
                button("Reprendre avec aide") { remainingMs = 10 * 60 * 1000L; showWatch(); runTimer() }
            }
        }
    }

    private fun showParents() {
        base("Espace parents")
        root.addView(label("Paramètres de démonstration", 22f), match())
        button("Langue : Français / العربية") {
            language = if (language == "fr") "ar" else "fr"
            Toast.makeText(this, "Langue sélectionnée : $language (interface bilingue en cours)", Toast.LENGTH_SHORT).show()
        }
        button("Durée : 10 minutes (fixe dans cette version)") {
            Toast.makeText(this, "La durée sera configurable dans une prochaine version.", Toast.LENGTH_SHORT).show()
        }
        button("Retour") { showHome() }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            timer?.cancel()
            showHome()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}

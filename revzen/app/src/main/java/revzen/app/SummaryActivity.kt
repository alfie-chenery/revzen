package revzen.app

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import revzen.app.api.ApiError
import revzen.app.api.ApiHandler
import revzen.app.api.Pet
import revzen.app.api.PetStatus
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class SummaryActivity : AppCompatActivity() {
    private lateinit var apiHandler: ApiHandler
    private lateinit var studyList : ArrayList<SessionData>
    private lateinit var studyRes: ApiHandler.StudyResult

    private lateinit var XP: TextView
    private lateinit var totalStudy: TextView
    private lateinit var totalBreak: TextView
    private lateinit var ratio: TextView
    private lateinit var petXP: TextView

    private lateinit var petImage: ImageView
    private lateinit var healthImage: ImageView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        studyList = intent.extras?.getParcelableArrayList("studyList")!!
        apiHandler = intent.extras?.getParcelable("handler")!!

        XP = findViewById(R.id.summaryXP)
        totalStudy = findViewById(R.id.summaryTotalStudy)
        totalBreak = findViewById(R.id.summaryTotalBreak)
        ratio = findViewById(R.id.summaryRatio)
        petXP = findViewById(R.id.summaryPetXP)

        petImage = findViewById(R.id.summaryPetImage)
        healthImage = findViewById(R.id.summaryPetHealth)

        studyRes = calculateResult(studyList)

        apiHandler.stopLiveRevision({},{_ -> })

        val random = Random()
        if (random.nextInt(STUDY_PET_THRESHOLD) < studyRes.xp) {
            apiHandler.givePet(Pet.values()[random.nextInt(2) + 1], this::successfulGivePet, this::givePetFailure)
        } else {
            apiHandler.logSession(studyRes, this::successfulStudyLog, this::studyLogFailure)
        }

        XP.text = "${studyRes.xp} XP"
        totalStudy.text = timeFormat(studyRes.total_study_time)
        totalBreak.text = timeFormat(studyRes.total_break_time)
        if(studyRes.total_break_time == 0){
            ratio.text = "N/A"
        } else {
            val ratioVal = studyRes.total_study_time.toDouble() / studyRes.total_break_time.toDouble()
            ratio.text = "${(ratioVal * 100.0).roundToInt() / 100.0}"
        }
    }

    override fun onBackPressed() {}

    fun goToMenu(_view: View) {
        finish()
    }

    @SuppressLint("SetTextI18n")
    fun successfulStudyLog(pet: PetStatus) {
        petImage.setImageResource(pet.petType.logoImage)
        healthImage.setImageResource(pet.health.image)
        petXP.text = "${pet.xp} XP"
        petImage.visibility = View.VISIBLE
        healthImage.visibility = View.VISIBLE
    }

    private fun studyLogFailure(error: ApiError) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(
                when (error) {
                    ApiError.NO_SUCH_USER -> R.string.login_failure_no_such_user
                    ApiError.WRONG_VERSION -> R.string.login_failure_outdated_api
                    else -> R.string.login_failure_unspecified_api_error
                }
            )
            setPositiveButton("Ok") { _, _ -> finish() }
            create()
            show()
        }
    }

    private fun successfulGivePet(new_pet: Pet) {
        AlertDialog.Builder(this).apply {
            setIcon(new_pet.studyImage)
            setTitle("Gained a new Pet!")
            setMessage("You have gained a new pet for your family! Study well & keep it healthy!")
            setPositiveButton("Ok") { _, _ -> }
            create()
            show()
        }
        apiHandler.logSession(studyRes, this::successfulStudyLog, this::studyLogFailure)
    }

    private fun givePetFailure(error: ApiError) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(
                when (error) {
                    ApiError.NO_SUCH_USER -> R.string.login_failure_no_such_user
                    ApiError.WRONG_VERSION -> R.string.login_failure_outdated_api
                    else -> R.string.login_failure_unspecified_api_error
                }
            )
            setPositiveButton("Ok") { _, _ -> finish() }
            create()
            show()
        }
    }
}
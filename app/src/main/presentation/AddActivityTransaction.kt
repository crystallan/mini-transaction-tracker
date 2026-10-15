package com.transaction.shared.presentation.transaction.add.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.transaction.shared.domain.model.TransactionType
import com.transaction.shared.utils.DateUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

// XML equivalent of AddTransactionScreen. Everything Compose did automatically via
// recomposition (state -> UI) has to be done by hand here: read state, push it onto views.
class AddTransactionActivity : AppCompatActivity() {

    private val viewModel: AddTransactionViewModel by viewModel()

    // findViewById is the XML-world replacement for "the composable just has the ref"
    private lateinit var amountLayout: com.google.android.material.textfield.TextInputLayout
    private lateinit var amountInput: com.google.android.material.textfield.TextInputEditText
    private lateinit var descriptionLayout: com.google.android.material.textfield.TextInputLayout
    private lateinit var descriptionInput: com.google.android.material.textfield.TextInputEditText
    private lateinit var chipCredit: Chip
    private lateinit var chipDebit: Chip
    private lateinit var dateButton: com.google.android.material.button.MaterialButton
    private lateinit var dateErrorText: android.widget.TextView
    private lateinit var saveButton: com.google.android.material.button.MaterialButton
    private lateinit var saveProgress: android.widget.ProgressBar
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        rootView = findViewById(android.R.id.content)
        bindViews()
        setupToolbarBackButton()
        setupInputListeners()
        setupTypeChips()
        setupDatePicker()
        setupSaveButton()
        observeUiState()
        observeOneOffEvents()
    }

    private fun bindViews() {
        amountLayout = findViewById(R.id.amountLayout)
        amountInput = findViewById(R.id.amountInput)
        descriptionLayout = findViewById(R.id.descriptionLayout)
        descriptionInput = findViewById(R.id.descriptionInput)
        chipCredit = findViewById(R.id.chipCredit)
        chipDebit = findViewById(R.id.chipDebit)
        dateButton = findViewById(R.id.dateButton)
        dateErrorText = findViewById(R.id.dateErrorText)
        saveButton = findViewById(R.id.saveButton)
        saveProgress = findViewById(R.id.saveProgress)
    }

    private fun setupToolbarBackButton() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { onNavigateBack() }
    }

    private fun onNavigateBack() {
        finish()
    }

    // Compose's onValueChange -> XML's addTextChangedListener. No visual transformation
    // widget exists in XML; you'd format the string manually inside the listener.
    private fun setupInputListeners() {
        amountInput.addTextChangedListener { text ->
            viewModel.onAmountChange(text?.toString().orEmpty())
        }
        descriptionInput.addTextChangedListener { text ->
            viewModel.onDescriptionChange(text?.toString().orEmpty())
        }
    }

    // Compose's FilterChip selected state -> ChipGroup single-selection + manual click listeners
    private fun setupTypeChips() {
        chipCredit.setOnClickListener { viewModel.onTypeChange(TransactionType.CREDIT) }
        chipDebit.setOnClickListener { viewModel.onTypeChange(TransactionType.DEBIT) }
    }

    // Compose's `remember { mutableStateOf(false) }` + DatePickerDialog -> MaterialDatePicker,
    // a fragment-based dialog since XML/View world has no in-place conditional composition
    private fun setupDatePicker() {
        dateButton.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
            picker.addOnPositiveButtonClickListener { millis ->
                val pickedDate = DateUtils.millisToLocalDate(millis)
                viewModel.onDateChange(DateUtils.combineDateWithCurrentTime(pickedDate))
            }
            picker.show(supportFragmentManager, "date_picker")
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener { viewModel.submitTransaction() }
    }

    // This whole function is what recomposition replaced. Compose diffs state and
    // re-draws only what changed; here every field is re-applied by hand on each emission.
    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    if (amountInput.text?.toString() != state.amount) {
                        amountInput.setText(state.amount)
                    }
                    amountLayout.error = state.amountError

                    if (descriptionInput.text?.toString() != state.description) {
                        descriptionInput.setText(state.description)
                    }
                    descriptionLayout.error = state.descriptionError

                    chipCredit.isChecked = state.type == TransactionType.CREDIT
                    chipDebit.isChecked = state.type == TransactionType.DEBIT

                    dateButton.text = DateUtils.formatDateOnly(state.date)

                    dateErrorText.text = state.dateError
                    dateErrorText.visibility = if (state.dateError != null) View.VISIBLE else View.GONE

                    saveButton.isEnabled = !state.isSubmitting
                    saveButton.visibility = if (state.isSubmitting) View.INVISIBLE else View.VISIBLE
                    saveProgress.visibility = if (state.isSubmitting) View.VISIBLE else View.GONE
                }
            }
        }
    }

    // Compose's LaunchedEffect(Unit) { viewModel.events.collectLatest { ... } } -> same
    // Flow-collection idea, just anchored to the Activity's lifecycle instead of a composable's
    private fun observeOneOffEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collectLatest { event ->
                    when (event) {
                        is AddTransactionViewEvent.Success -> onNavigateBack()
                        is AddTransactionViewEvent.Error ->
                            Snackbar.make(rootView, event.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
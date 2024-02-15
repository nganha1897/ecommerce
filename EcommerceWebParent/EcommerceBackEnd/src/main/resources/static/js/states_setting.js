var buttonLoadForStates;
var dropdownCountryForStates;
var dropdownState;
var buttonAddState;
var buttonUpdateState;
var buttonDeleteState;
var labelStateName;
var fieldStateName;

$(document).ready(function() {
	buttonLoadForStates = $("#buttonLoadCountriesForState");
	dropdownCountryForStates = $("#dropdownCountriesForState");
	dropdownState = $("#dropdownStates");
	buttonAddState = $("#buttonAddState");
	buttonUpdateState = $("#buttonUpdateState");
	buttonDeleteState = $("#buttonDeleteState");
	labelStateName = $("#labelStateName");
	fieldStateName = $("#fieldStateName");

	buttonLoadForStates.click(function() {
		loadCountriesForState();
	});

	dropdownCountryForStates.on("change", function() {
		loadStates();
	});

	dropdownState.on("change", function() {
		changeFormToSelectedState();
	});

	buttonAddState.click(function() {
		if (buttonAddState.val() == "Add") {
			addState();
		} else {
			changeFormStateToNewState();
		}
	});

	buttonUpdateState.click(function() {
		updateState();
	});

	buttonDeleteState.click(function() {
		deleteState();
	});
});

function deleteState() {
	stateId = dropdownState.val();
	url = contextPath + "states/delete/" + stateId;
	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function() {
		$("#dropdownStates option[value='" + stateId + "']").remove();
		changeFormStateToNewState();
		showToastMessage("The state has been successfully deleted!");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server");
	});
}

function changeFormStateToNewState() {
	buttonAddState.val("Add");
	labelStateName.text("State Name");
	buttonUpdateState.prop("disabled", true);
	buttonDeleteState.prop("disabled", true);

	fieldStateName.val("").focus();
}

function updateState() {
	if (!validateFormState()) {
		return false;
	}
	url = contextPath + "states/save";
	stateName = fieldStateName.val();
	stateId = dropdownState.val();
	countryId = dropdownCountryForStates.val();
	countryName = dropdownCountryForStates.text();
	jsonData = { id: stateId, name: stateName, country: { id: countryId, name: countryName } };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);

		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId) {
		$("#dropdownStates option:selected").val(stateId);
		$("#dropdownStates option:selected").text(stateName);
		showToastMessage("The state has been updatedd!");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server");
	});
}

function validateFormState() {
	formState = document.getElementById("formState");
	if (!formState.checkValidity()) {
		formState.reportValidity();
		return false;
	}
	return true;
}

function addState() {
	if (!validateFormState()) {
		return false;
	}
	url = contextPath + "states/save";
	stateName = fieldStateName.val();
	countryId = dropdownCountryForStates.val();
	countryName = dropdownCountryForStates.text();
	jsonData = { name: stateName, country: { id: countryId, name: countryName } };

	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeaderName, csrfValue);

		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId) {
		selectNewlyAddedState(stateId, stateName);
		showToastMessage("The new state has been added!");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server");
	});
}

function selectNewlyAddedState(stateId, stateName) {
	optionValue = stateId;
	$("<option>").val(optionValue).text(stateName).appendTo(dropdownState);

	$("#dropdownStates option[value='" + optionValue + "']").prop("selected", true);
	fieldStateName.val("").focus();
}

function changeFormToSelectedState() {
	buttonAddState.prop("value", "New");
	buttonUpdateState.prop("disabled", false);
	buttonDeleteState.prop("disabled", false);

	labelStateName.text("Selected State")
	selectedStateName = $("#dropdownStates option:selected").text();
	fieldStateName.val(selectedStateName);
}

function loadStates() {
	countryId = dropdownCountryForStates.val();
	url = contextPath + "states/list_by_country/" + countryId;
	$.get(url, function(responseJSON) {
		dropdownState.empty();

		$.each(responseJSON, function(index, state) {
			optionValue = state.id;
			$("<option>").val(optionValue).text(state.name).appendTo(dropdownState);
		});
	}).done(function() {
		showToastMessage("All states have been successfully loaded!");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server");
	});
}

function loadCountriesForState() {
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON) {
		dropdownCountryForStates.empty();

		$.each(responseJSON, function(index, country) {
			optionValue = country.id;
			$("<option>").val(optionValue).text(country.name).appendTo(dropdownCountryForStates);
		});
		if (responseJSON.length > 0) {
			dropdownCountryForStates.val(responseJSON[0].id);
			loadStates();
		}
	}).done(function() {
		buttonLoadForStates.val("Refresh Country List");
		showToastMessage("All countries have been successfully loaded!");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to server");
	});

}
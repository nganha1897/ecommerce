var dropdownCountry;
var dataListState;
var fieldState;

$(document).ready(function() {
	dropdownCountry = $("#country");
	dataListState = $("#listStates");
	fieldState = $("#state");

    loadStatesForCountry();
	dropdownCountry.on("change", function() {
		fieldState.val("").focus();
		loadStatesForCountry();
	});
});

function loadStatesForCountry() {
	selectedCountry = $("#country option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "states/list_by_country/" + countryId;
	$.get(url, function(responseJSON) {
		dataListState.empty();

		$.each(responseJSON, function(index, state) {
			$("<option>").val(state.name).text(state.name).appendTo(dataListState);
		});

	});
}

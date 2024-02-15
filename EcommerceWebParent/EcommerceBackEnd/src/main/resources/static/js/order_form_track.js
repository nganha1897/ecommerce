var nextCount;

$(document).ready(function() {
	nextCount = $(".hiddenTrackId").length;
	$("#trackList").on("click", ".linkTrackRemove", function(e) {
		e.preventDefault();
		removeTrack($(this));
		updateTrackCountNumbers();
	});

	$("#track").on("click", "#linkAddTrack", function(e) {
		e.preventDefault();
		addTrack();
		updateTrackCountNumbers();
	});

	$("#trackList").on("change", ".dropdownStatus", function(e) {
		dropdownList = $(this);
		rowNumber = dropdownList.attr("rowNumber");
		selectedOption = $("option:selected", dropdownList);

		defaultNote = selectedOption.attr("defaultDescription");
		$("#trackNote" + rowNumber).text(defaultNote);
	});
});

function removeTrack(link) {
	rowNumber = link.attr("rowNumber");
	$("#rowTrack" + rowNumber).remove();
	$("#blankTrack" + rowNumber).remove();


}

function updateTrackCountNumbers() {
	$(".divTrackCount").each(function(index, element) {
		element.innerHTML = "" + (index + 1);
	});
}

function addTrack() {
	nextCount++;
	rowId = "rowTrack" + nextCount;
	blankTrackId = "blankTrack" + nextCount;
	currentDateTime = formatCurrentDateTime();
	noteId = "trackNote" + nextCount;

	htmlCode = `
	    <div class="row border rounded p-1" id="${rowId}">
			<input type="hidden" name="trackId" class="hiddenTrackId" value="0"/>
				<div class="col-2">
					<div class="divTrackCount">${nextCount}</div>
					<div>
					    <a class="fas fa-trash icon-dark linkTrackRemove" href=""
						    rowNumber="${nextCount}">
						</a>
					</div>
				</div>

				<div class="col-10">
					<div class="form-group row">
						<label class="col-form-label col-1">Time</label>
						<div class="col">
							<input type="datetime-local" class="form-control" style="max-width: 300px" required name="trackDate"
							    value="${currentDateTime}"/>
						</div>
					</div>

					<div class="form-group row">
						<label class="col-form-label col-1">Status</label>
						<div class="col">
						    <select class="form-control dropdownStatus" style="max-width: 300px"  name="trackStatus"
						        rowNumber="${nextCount}">
		`;

	htmlCode += $("#trackStatusOption").clone().html();

	htmlCode += `	
	                        </select>	
						</div>
					</div>

					<div class="form-group row">
						<label class="col-form-label col-1">Notes</label>
						<div class="col">
							<textarea rows="2" cols="10" class="form-control" style="max-width: 300px" required name="trackNote"
							    id="${noteId}">
							</textarea>
						</div>
					</div>
				</div>
		</div>
		<div class="row" id="${blankTrackId}">&nbsp;</div>
	`;
	$("#trackList").append(htmlCode);
}

function formatCurrentDateTime() {
	date = new Date();
	year = date.getFullYear();
	month = date.getMonth() + 1;
	day = date.getDate();
	hour = date.getHours();
	minute = date.getMinutes();
	second = date.getSeconds();

	if (month < 10) month = "0" + month;
	if (day < 10) day = "0" + day;

	if (hour < 10) hour = "0" + hour;
	if (minute < 10) minute = "0" + minute;
	if (second < 10) second = "0" + second;

	return year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;
}
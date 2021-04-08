function searchController() {
    let dropDown = document.getElementById('searchItem');
    let datePickerValue = document.getElementById('datePicker').value;

    var dropDownValue = dropDown.options[dropDown.selectedIndex].text;

    //set tile data
    violationTileService(dropDownValue, datePickerValue);
    vbuService(dropDownValue, datePickerValue);
    commitTileService(dropDownValue, datePickerValue);
    cbuService(dropDownValue, datePickerValue);
}
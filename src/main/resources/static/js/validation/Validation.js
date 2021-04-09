function btnSearchValidation(dropDown, datePickerValue) {
    if (datePickerValue == "" || datePickerValue == null) {
        return false;
    }
    if (typeof dropDown.options[dropDown.selectedIndex] === "undefined" || datePickerValue === "undefined") {
        return false;
    }
    else {
        return true;
    }
}
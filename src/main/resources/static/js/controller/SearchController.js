function searchController() {

    //clear data in table
    $("#tblVioation").children().remove();
    $("#tblVbU").children().remove();
    $("#tblCommits").children().remove();
    $("#tblCbU").children().remove();
    document.getElementById('commitAPILimitaion').innerHTML = "";
    document.getElementById('commitCbUAPILimitaion').innerHTML = "";

    //check table empty and set data loading animation
    checkTableEmpty();

    let dropDown = document.getElementById('searchItem');
    let datePickerValue = document.getElementById('datePicker').value;


    //validation
    const validate = btnSearchValidation(dropDown, datePickerValue);

    if (validate) {

        var dropDownValue = dropDown.options[dropDown.selectedIndex].text;
        //set project name
        document.getElementById('projectName').innerHTML = "Project : " + dropDownValue;
        //set tile data
        let val = violationTileService(dropDownValue, datePickerValue);
        vbuService(dropDownValue, datePickerValue);
        commitTileService(dropDownValue, datePickerValue);
        cbuService(dropDownValue, datePickerValue);

        //true == project scanned hide project not scanned text
        projectNotScannedController(true);
    }
    else {
        alert("Please check whether the project and date are selected !");
    }
}
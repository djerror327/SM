
function projectNotScannedController(value) {
    //set project not sacanned text at page loading

    //true == project scanned
    //false == project not scanned

    if (value) {
        const projectNotScanTxtList = document.getElementsByClassName('projectNotScanTxt');
        for (element in projectNotScanTxtList) {
            projectNotScanTxtList[element].innerHTML = "";
        }
    } else {
        const projectNotScanTxtList = document.getElementsByClassName('projectNotScanTxt');
        for (element in projectNotScanTxtList) {
            projectNotScanTxtList[element].innerHTML = "Project not scanned !";
        }
    }
}
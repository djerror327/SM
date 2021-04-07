async function loadVialationData() {
    const violationURL = vioalionAPI('SonarQubeOpenViolationMonitor', '2021-03');
    let violationData;
    await fetch(violationURL)
        .then(res => res.json())
        .then(data => violationData = JSON.stringify(data));

    //set violation tile data
    let jsonViolation = JSON.parse(violationData);


    let branchesObj = jsonViolation[0];
    console.log("branchesObj ",branchesObj);

    for (branches in branchesObj) {
        console.log("barnches ", branchesObj[branches]);
        for(branch in branches){
            console.log("barnch ", branches[branch]);
        }
        // alert(branches[branch])
    }

}

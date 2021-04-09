async function violationTileService(projectKey, date) {
    const violationURL = vioalionAPI(projectKey, date);
    let violationData;
    await fetch(violationURL)
        .then(res => res.json())
        .then(data => violationData = JSON.stringify(data));

    //set violation tile data
    let jsonViolation = JSON.parse(violationData);
    let branchesObj = jsonViolation[0];

    //get table tag
    const table = document.getElementById('tblVioation');
    for (branches in branchesObj) {
        let branchesName = branchesObj[branches];

        for (branch in branchesName) {
            const tblRow = document.createElement('tr');
            const tblCellName = document.createElement('td');
            const tblCellBarnch = document.createElement('td');
            const tblBranchName = document.createTextNode(branch);
            const tblBranchValue = document.createTextNode(branchesName[branch]);

            //set table data
            tblCellName.appendChild(tblBranchName);
            tblCellBarnch.appendChild(tblBranchValue);
            tblRow.appendChild(tblCellName);
            tblRow.appendChild(tblCellBarnch);
            table.appendChild(tblRow);

        }
    }
    //stop data loading animation
    checkTableEmpty();
    //enable search button after data loading
    vilationTileLoaded = true;
    btnSearchCheckDisable();
}

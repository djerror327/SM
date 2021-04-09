async function commitTileService(projectKey, date) {
    const SCMURL = scmAPI(projectKey, date);
    let commitData;
    await fetch(SCMURL)
        .then(res => res.json())
        .then(data => commitData = JSON.stringify(data));

    // set violation tile data
    let jsonCommit = JSON.parse(commitData);
    let branchesObj = jsonCommit[0];

    // //get table tag
    const table = document.getElementById('tblCommits');
    const commitAPILimitaion = document.getElementById('commitAPILimitaion');
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
    commitAPILimitaion.innerHTML = 'Support main branch only. API limitation !';

    //stop data loading animation
    checkTableEmpty();
    //enable search button after data loading
    commitsTileLoaded = true;
    btnSearchCheckDisable();
}
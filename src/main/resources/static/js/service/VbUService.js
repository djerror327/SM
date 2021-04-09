async function vbuService(projectKey, date) {
    const violationURL = vioalionAPI(projectKey, date);
    let violationData;
    await fetch(violationURL)
        .then(res => res.json())
        .then(data => violationData = JSON.stringify(data));

    // set violation tile data
    let jsonViolation = JSON.parse(violationData);
    let userObj = jsonViolation[1];

    //get table tag
    const table = document.getElementById('tblVbU');
    for (usersBranches in userObj) {
        let branchesName = userObj[usersBranches];


        for (branch in branchesName) {
            let userBranch = branchesName[branch];

            //set table headers (branches name)
            const tblRowHeaderSpace = document.createElement('tr');
            const tblRowHeader = document.createElement('tr');
            const tblHeaderBranch = document.createElement('th');
            const tblCellHeader = document.createTextNode(branch);

            tblHeaderBranch.appendChild(tblCellHeader);
            tblRowHeader.appendChild(tblHeaderBranch);
            table.appendChild(tblRowHeaderSpace);
            table.appendChild(tblRowHeader);
            table.appendChild(tblRowHeaderSpace);

            for (users in userBranch) {
                const tblRow = document.createElement('tr');
                const tblCellName = document.createElement('td');
                const tblCellValue = document.createElement('td');
                //create a additional space
                const tblAuthorSapce = document.createElement('td');
                const tblAuthorhName = document.createTextNode(users);
                const tblAuthorValue = document.createTextNode(userBranch[users]);

                //set table data
                tblCellName.appendChild(tblAuthorhName);
                tblCellValue.appendChild(tblAuthorValue);

                tblRow.appendChild(tblAuthorSapce);
                tblRow.appendChild(tblCellName);
                tblRow.appendChild(tblCellValue);
                table.appendChild(tblRow);
            }
        }
    }
    //stop data loading animation
    checkTableEmpty();
     //enable search button after data loading
     VbUTileLoaded = true;
     btnSearchCheckDisable();
}
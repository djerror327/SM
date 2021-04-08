async function cbuService() {
    const SCMURL = scmAPI('SonarQubeOpenViolationMonitor', '2021-03');
    let commitData;
    await fetch(SCMURL)
        .then(res => res.json())
        .then(data => commitData = JSON.stringify(data));

    // set violation tile data
    let jsonCommit = JSON.parse(commitData);
    let userObj = jsonCommit[1];

    // console.log("user obj ",userObj)
    // //get table tag
    const table = document.getElementById('tblCbU');
    const commitAPILimitaion = document.getElementById('commitCbUAPILimitaion');
    for (usersBranches in userObj) {
        let branchesName = userObj[usersBranches];
        // console.log("users",branchesName)

        for (branch in branchesName) {
            let userBranch = branchesName[branch];
            console.log('branch name ', branch)
            for (users in userBranch) {
                // let authors=users;
                console.log("authors", branch, users, userBranch[users])
                const tblRowHeader = document.createElement('tr');
                const tblRow = document.createElement('tr');
                const tblHeaderBranch = document.createElement('th');
                const tblCellName = document.createElement('td');
                const tblCellValue = document.createElement('td');
                const tblCellHeader = document.createTextNode(branch);
                const tblAuthorhName = document.createTextNode(users);
                const tblAuthorValue = document.createTextNode(userBranch[users]);

                //set table data
                tblHeaderBranch.appendChild(tblCellHeader);
                tblCellName.appendChild(tblAuthorhName);
                tblCellValue.appendChild(tblAuthorValue);

                tblRowHeader.appendChild(tblHeaderBranch);
                tblRow.appendChild(tblCellName);
                tblRow.appendChild(tblCellValue);
                table.appendChild(tblRowHeader);
                table.appendChild(tblRow);
            }
        }
    }
    commitAPILimitaion.innerHTML='Support Main branch only. API limitation';

}
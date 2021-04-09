function checkTableEmpty() {

    //violation tile data loading
    if ($('#tblVioation tr').length == 0) {

        document.getElementById('violationTileLoading').innerHTML = "Data Loading...";
        // console,console.log("table is empty",val);
    }
    else {
        document.getElementById('violationTileLoading').innerHTML = "";
    }

    //VbU tile data loading
    if ($('#tblVbU tr').length == 0) {

        document.getElementById('vbuTileLoading').innerHTML = "Data Loading...";
        // console,console.log("table is empty",val);
    }
    else {
        document.getElementById('vbuTileLoading').innerHTML = "";
    }

    //Commits tile data loading
    if ($('#tblCommits tr').length == 0) {

        document.getElementById('CommitsTileLoading').innerHTML = "Data Loading...";
        // console,console.log("table is empty",val);
    }
    else {
        document.getElementById('CommitsTileLoading').innerHTML = "";
    }

    //Commits tile data loading
    if ($('#tblCbU tr').length == 0) {

        document.getElementById('cbuTileLoading').innerHTML = "Data Loading...";
        // console,console.log("table is empty",val);
    }
    else {
        document.getElementById('cbuTileLoading').innerHTML = "";
    }
}
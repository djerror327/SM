function checkTableEmpty() {
    //violation tile data loading
    if ($('#tblVioation tr').length == 0) {
        document.getElementById('violationTileLoading').style.visibility = "visible";
    }
    else {
        document.getElementById('violationTileLoading').style.visibility = "hidden";
    }

    //VbU tile data loading
    if ($('#tblVbU tr').length == 0) {

        document.getElementById('vbuTileLoading').style.visibility = "visible";
    }
    else {
        document.getElementById('vbuTileLoading').style.visibility = "hidden";
    }

    //Commits tile data loading
    if ($('#tblCommits tr').length == 0) {
        document.getElementById('CommitsTileLoading').style.visibility = "visible";
    }
    else {
        document.getElementById('CommitsTileLoading').style.visibility = "hidden";
    }

    //Commits tile data loading
    if ($('#tblCbU tr').length == 0) {

        document.getElementById('cbuTileLoading').style.visibility = "visible";
    }
    else {
        document.getElementById('cbuTileLoading').style.visibility = "hidden";
    }
}
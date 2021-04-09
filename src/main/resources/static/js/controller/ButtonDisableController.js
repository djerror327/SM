let vilationTileLoaded = false;
let VbUTileLoaded = false;
let commitsTileLoaded = false;
let CbUTileLoaded = false;

//disable search button until all tiles loading finish
function btnSearchCheckDisable() {
    if (vilationTileLoaded && VbUTileLoaded && commitsTileLoaded && CbUTileLoaded) {
        document.getElementById("btnSearch").disabled = false;
    } else {
        document.getElementById("btnSearch").disabled = true;
    }
}
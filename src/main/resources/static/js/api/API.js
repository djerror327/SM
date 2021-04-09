// const hostName = "http://localhost:" + 8080;
const hostName = window.location.protocol + "//" + window.location.host;

function projectAPI() {
    return hostName + "/v1/projects";
}

function vioalionAPI(prjectKey, date) {
    return hostName + "/v1/violations/" + prjectKey + "/" + date + "";
}

function scmAPI(prjectKey, date) {
    return hostName + "/v1/scm/commits/" + prjectKey + "/" + date + "";
}
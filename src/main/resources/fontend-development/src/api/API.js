//change this section for prot define when run ui separatly

// const hostName = window.location.protocol + "//" + window.location.host;
const hostName = "http://localhost:" + 8080;

export function projectAPI() {
    return hostName + "/v1/projects";
}

export function vioalionAPI(prjectKey, date) {
    return hostName + "/v1/violations/" + prjectKey + "/" + date + "";
}

export function scmAPI(prjectKey, date) {
    return hostName + "/v1/scm/commits/" + prjectKey + "/" + date + "";
}
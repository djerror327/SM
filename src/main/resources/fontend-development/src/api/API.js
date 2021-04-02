const frontendPort = 3000;
const backendPort = 8080;

function host() {
    if (window.location.port == frontendPort) {
        //when frondend is running separatly from backend
        return "http://localhost:"+backendPort;
    }
    else {
        //when fronend run in springboot app
        const hostName = window.location.protocol + "//" + window.location.host;
        return hostName;
    }
}

export function projectAPI() {
    return "http://localhost:8080" + "/v1/projects";
}

export function vioalionAPI(prjectKey, date) {
    return "http://localhost:8080" + "/v1/violations/" + prjectKey + "/" + date + "";
}

export function scmAPI(prjectKey, date) {
    console.log("host name ========="+host());
    return +host() + "/v1/scm/commits/" + prjectKey + "/" + date + "";
}
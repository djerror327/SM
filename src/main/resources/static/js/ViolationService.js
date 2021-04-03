
//  <script type="module" src="./API"></script>

// function a() {
//     const violationURL = vioalionAPI('SonarQubeOpenViolationMonitor', '2021-03')
//     let payload;
//     console.log(violationURL)
//     fetch(violationURL)
//         .then(resp => resp.json())
//         .then(data => payload=data)
//     console.log(payload)
// }

var responce;

function loadDoc() {
    const violationURL = vioalionAPI('SonarQubeOpenViolationMonitor', '2021-03')
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            responce = this.responseText;
        }
    };
    xhttp.open("GET", "/v1/violations/SonarQubeOpenViolationMonitor/2021-03", true);
    xhttp.send();
}

function a(){
    console.log(responce)
}

async function loadSCMData() {
    const SCMURL = scmAPI('SonarQubeOpenViolationMonitor', '2021-03');
    await fetch(SCMURL)
        .then(res => res.json())
        .then(data => document.getElementById("a").innerHTML = JSON.stringify(data));
}
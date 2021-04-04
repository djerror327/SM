async function loadProjectList() {
    const projectURL = projectAPI();
    await fetch(projectURL)
        .then(res => res.json())
        .then(data => document.getElementById("a").innerHTML = JSON.stringify(data));
}
let objArr = [];
async function loadProjectList() {
    const projectURL = projectAPI();

    await fetch(projectURL)
        .then(res => res.json())
        .then(data => {
            const jsonArr = JSON.stringify(data)
            objArr = JSON.parse(jsonArr);

            var select = document.getElementById('searchItem');
            objArr.forEach((value, index) => {
                var option = document.createElement("option");
                option.innerHTML = value;
                select.appendChild(option);

            });
        });
}
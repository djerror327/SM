function search() {
    var search = document.getElementById('search');
    // objArr is a global varialbe set in /js/service/ProjectService.js
    objArr.forEach((value, index) => {
        console.log(value);
        if (value == search.value) {
            $('#searchItem option:contains(' + value + ')').prop({ selected: true });
        }
    });
}
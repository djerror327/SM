import React, { useEffect, useState } from "react";
import { scmAPI } from '../api/API';

function Body() {
    const [songs, setSongs] = useState({})
    useEffect(() => { violationAPI(); }, []);

    const violationAPI = () => {
        fetch(scmAPI('SonarQubeOpenViolationMonitor', '2021-03'))
            .then(resp => resp.json())
            .then(data => setSongs(data))
    };
    console.log({ songs })
    console.log("port " + window.location.port)
    return (
        <div>
            <p>this the body of the page {`${songs.master}`}</p>
        </div>
    );
}
function getData() {

}
export default Body;
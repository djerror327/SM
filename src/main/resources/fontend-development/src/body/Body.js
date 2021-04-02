import React, { useEffect, useState } from "react";

function Body() {
    const [count, setCount] = useState(0);
    const [songs, setSongs] = useState({})
    useEffect(() => { violationAPI();},[]);
    
    const violationAPI =()=>{
        fetch('http://localhost:8080/v1/scm/commits/SonarQubeOpenViolationMonitor/2021-03')
    .then(resp => resp.json())
    .then(data => setSongs(data))
    };
    console.log({songs})
    return (
        <div>
            <p>this the body of the page {`${songs.master}`}</p>
        </div>
    );
}
function getData() {

}
export default Body;
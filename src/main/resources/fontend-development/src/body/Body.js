import React, { useEffect, useState } from "react";
import Violation from '../service/ViolationService';
// import { scmAPI } from '../api/API';

function Body() {
    // const [scmData, setSCMData] = useState({})
    // useEffect(() => { violationAPI(); }, []);

    // const violationAPI = () => {
    //     fetch(scmAPI('SonarQubeOpenViolationMonitor', '2021-03'))
    //         .then(resp => resp.json())
    //         .then(data => setSCMData(data))
    // };
    // console.log({ scmData })

    return (
        <div>
            <span>this the body of the page <Violation /></span>
        </div>
    );
}
export default Body;
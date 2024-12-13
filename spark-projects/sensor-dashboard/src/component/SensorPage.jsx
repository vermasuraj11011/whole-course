import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {useParams} from 'react-router-dom';
import SensorDetails from './SensorDetails';

function SensorPage() {
    const {sensorId} = useParams();
    const [sensor, setSensor] = useState(null);

    useEffect(() => {
        axios.get(`http://localhost:8080/api/aggregated-data/${sensorId}`)
            .then(response => setSensor(response.data[0]))
            .catch(error => console.error('Error fetching sensor data:', error));
    }, [sensorId]);

    return (
        <div className="sensor-div">
            <h1>Sensor Details</h1>
            {sensor ? <SensorDetails sensor={sensor}/> : <p>Loading...</p>}
        </div>
    );
}

export default SensorPage;
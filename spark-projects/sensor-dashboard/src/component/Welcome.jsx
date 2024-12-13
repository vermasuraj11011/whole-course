import React from 'react';
import { useNavigate } from 'react-router-dom';
import './Welcome.css';
// import sensorImage from './sensor-image.jpg'; // Import your temperature sensor image

function Welcome() {
    const navigate = useNavigate();

    const goToDashboard = () => {
        navigate('/dashboard');
    };

    return (
        <div className="welcome">
            <h1>Welcome to the Sensor Dashboard</h1>
            {/*<img src={sensorImage} alt="Temperature Sensor" />*/}
            <button onClick={goToDashboard}>Go to Dashboard</button>
        </div>
    );
}

export default Welcome;

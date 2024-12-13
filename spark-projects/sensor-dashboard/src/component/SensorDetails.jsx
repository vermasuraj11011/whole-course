import React from 'react';
import './SensorDetails.css';

function SensorDetails({sensor}) {
    return (
        <div className="sensor-details">
            <h2>{sensor.sensorId}</h2>
            <table>
                <tbody>
                <tr>
                    <th>Avg Temperature</th>
                    <td>{sensor.avgTemperature.toFixed(2)}</td>
                </tr>
                <tr>
                    <th>Avg Humidity</th>
                    <td>{sensor.avgHumidity.toFixed(2)}</td>
                </tr>
                <tr>
                    <th>Min Temperature</th>
                    <td>{sensor.minTemperature.toFixed(2)}</td>
                </tr>
                <tr>
                    <th>Max Temperature</th>
                    <td>{sensor.maxTemperature.toFixed(2)}</td>
                </tr>
                <tr>
                    <th>Min Humidity</th>
                    <td>{sensor.minHumidity.toFixed(2)}</td>
                </tr>
                <tr>
                    <th>Max Humidity</th>
                    <td>{sensor.maxHumidity.toFixed(2)}</td>
                </tr>
                <tr>
                    <th>Weight</th>
                    <td>{sensor.weight}</td>
                </tr>
                </tbody>
            </table>
        </div>
    );
}

export default SensorDetails;

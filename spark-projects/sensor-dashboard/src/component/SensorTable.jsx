import React, {useState} from 'react';
import {Link} from 'react-router-dom';
import './SensorTable.css';
import Pagination from './Pagination';

function SensorTable({data}) {
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 10;

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const startIndex = (currentPage - 1) * itemsPerPage;
    const paginatedData = data.slice(startIndex, startIndex + itemsPerPage);
    const totalPages = Math.ceil(data.length / itemsPerPage);

    return (
        <div>
            <table>
                <thead>
                <tr>
                    <th>Sensor ID</th>
                    <th>Avg Temperature</th>
                    <th>Avg Humidity</th>
                    <th>Min Temperature</th>
                    <th>Max Temperature</th>
                    <th>Min Humidity</th>
                    <th>Max Humidity</th>
                    <th>Weight</th>
                </tr>
                </thead>
                <tbody>
                {paginatedData.map(sensor => (
                    <tr key={sensor.sensorId}>
                        <td>
                            <Link to={`/dashboard/${sensor.sensorId}`}>{sensor.sensorId}</Link>
                        </td>
                        <td>{sensor.avgTemperature}</td>
                        <td>{sensor.avgHumidity}</td>
                        <td>{sensor.minTemperature}</td>
                        <td>{sensor.maxTemperature}</td>
                        <td>{sensor.minHumidity}</td>
                        <td>{sensor.maxHumidity}</td>
                        <td>{sensor.weight}</td>
                    </tr>
                ))}
                </tbody>
            </table>
            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
            />
        </div>
    );
}

export default SensorTable;
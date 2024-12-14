import React, {useState} from 'react';
import {Link} from 'react-router-dom';
import axios from 'axios';
import './SensorTable.css';
import Pagination from './Pagination';

function SensorTable({data, setData}) {
    const [currentPage, setCurrentPage] = useState(1);
    const [searchQuery, setSearchQuery] = useState('');
    const [loading, setLoading] = useState(false);
    const itemsPerPage = 10;

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleSearchChange = (event) => {
        setSearchQuery(event.target.value);
        setCurrentPage(1); // Reset to the first page on new search
    };

    const handleRefresh = () => {
        setLoading(true);
        axios.get('http://localhost:8080/api/aggregated-data')
            .then(response => {
                setData(response.data);
                setLoading(false);
                setCurrentPage(1); // Reset to the first page on refresh
            })
            .catch(error => {
                console.error('Error fetching data:', error);
                setLoading(false);
            });
    };

    const filteredData = data.filter(sensor =>
        sensor.sensorId.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const startIndex = (currentPage - 1) * itemsPerPage;
    const paginatedData = filteredData.slice(startIndex, startIndex + itemsPerPage);
    const totalPages = Math.ceil(filteredData.length / itemsPerPage);

    return (
        <div>
            <div className="search-bar">
                <input
                    type="text"
                    placeholder="Search by Sensor ID"
                    value={searchQuery}
                    onChange={handleSearchChange}
                    className="search-input"
                />
                <button onClick={handleRefresh} className="refresh-button">Refresh</button>
            </div>
            {loading ? (
                <p>Loading...</p>
            ) : (
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
            )}
            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
            />
        </div>
    );
}

export default SensorTable;
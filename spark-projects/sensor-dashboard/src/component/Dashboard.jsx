import React, {useState, useEffect} from 'react';
import axios from 'axios';
import SensorTable from './SensorTable.jsx';

function Dashboard() {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axios.get('http://localhost:8080/api/aggregated-data')
            .then(response => {
                setData(response.data);
                setLoading(false);
            })
            .catch(error => {
                console.error('Error fetching data:', error);
                setLoading(false);
            });
    }, []);

    return (
        <div className="App">
            <h1>Sensor Dashboard</h1>
            {loading ? <p>Loading...</p> : <SensorTable data={data}/>}
        </div>
    );
}

export default Dashboard;
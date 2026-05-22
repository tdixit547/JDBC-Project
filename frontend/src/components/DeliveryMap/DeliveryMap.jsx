import React, { useState, useEffect, useRef } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet';
import L from 'leaflet';
import { assets } from '../../assets/assets';
import 'leaflet/dist/leaflet.css';
import './DeliveryMap.css';

// Fix for default marker icons moved inside component useEffect

// Custom Icons moved inside component

// Component to update map center
function MapUpdater({ center }) {
    const map = useMap();
    useEffect(() => {
        map.setView(center, map.getZoom());
    }, [center, map]);
    return null;
}

const DeliveryMap = ({ onClose }) => {
    // Mock coordinates (e.g., New York City area)
    const startPos = [40.7128, -74.0060]; // Restaurant
    const endPos = [40.7484, -73.9857];   // Customer (Empire State Building approx)
    
    const [scooterPos, setScooterPos] = useState(startPos);
    const [progress, setProgress] = useState(0);

    // Create icons safely inside the component (memoized)
    const scooterIcon = React.useMemo(() => new L.DivIcon({
        html: '<div style="font-size: 24px;">🛵</div>',
        className: 'custom-div-icon',
        iconSize: [30, 30],
        iconAnchor: [15, 15]
    }), []);

    const homeIcon = React.useMemo(() => new L.DivIcon({
        html: '<div style="font-size: 24px;">🏠</div>',
        className: 'custom-div-icon',
        iconSize: [30, 30],
        iconAnchor: [15, 15]
    }), []);

    useEffect(() => {
        // Fix for default marker icons in React Leaflet (run once)
        delete L.Icon.Default.prototype._getIconUrl;
        L.Icon.Default.mergeOptions({
            iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
            iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
        });
    }, []);

    useEffect(() => {
        const duration = 10000; // 10 seconds for full trip
        const intervalTime = 100;
        const steps = duration / intervalTime;
        let currentStep = 0;

        const timer = setInterval(() => {
            currentStep++;
            const newProgress = Math.min(currentStep / steps, 1);
            setProgress(newProgress);

            // Interpolate position
            const lat = startPos[0] + (endPos[0] - startPos[0]) * newProgress;
            const lng = startPos[1] + (endPos[1] - startPos[1]) * newProgress;
            setScooterPos([lat, lng]);

            if (newProgress >= 1) {
                clearInterval(timer);
            }
        }, intervalTime);

        return () => clearInterval(timer);
    }, []);

    return (
        <div className="delivery-map-modal">
            <div className="delivery-map-content">
                <div className="map-header">
                    <h3>Live Order Tracking</h3>
                    <div className="map-header-actions">
                         <a 
                            href={`https://www.google.com/maps/dir/?api=1&destination=${endPos[0]},${endPos[1]}`} 
                            target="_blank" 
                            rel="noopener noreferrer" 
                            className="google-maps-btn"
                            title="Open in Google Maps"
                        >
                            <img src={assets.selector_icon} alt="Google Maps" style={{width: '20px', height: '20px', marginRight: '5px'}}/> 
                            Open in Maps
                        </a>
                        <button onClick={onClose} className="close-map-btn">&times;</button>
                    </div>
                </div>
                <div className="map-container-wrapper">
                    <MapContainer center={scooterPos} zoom={13} style={{ height: '100%', width: '100%' }}>
                        <TileLayer
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                        />
                        <MapUpdater center={scooterPos} />
                        
                        {/* Home Marker */}
                        <Marker position={endPos} icon={homeIcon}>
                            <Popup>Your Location</Popup>
                        </Marker>

                        {/* Scooter Marker */}
                        <Marker position={scooterPos} icon={scooterIcon}>
                            <Popup>Your Order is Here!</Popup>
                        </Marker>
                    </MapContainer>
                </div>
                <div className="delivery-status">
                    <p>{progress < 1 ? "Driver is on the way..." : "Driver has arrived!"}</p>
                    <div className="status-bar">
                        <div className="status-fill" style={{ width: `${progress * 100}%` }}></div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DeliveryMap;

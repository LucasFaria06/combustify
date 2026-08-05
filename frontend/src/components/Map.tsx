'use client';

import { useEffect, useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { GasStation } from '@/hooks/useGasStations';

// Fix Leaflet icon issue
const defaultIcon = L.icon({
  iconUrl: '/images/marker-icon.png',
  iconRetinaUrl: '/images/marker-icon-2x.png',
  shadowUrl: '/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
});

L.Marker.prototype.setIcon(defaultIcon);

interface MapProps {
  stations: GasStation[];
  center?: [number, number];
  onStationClick: (station: GasStation) => void;
  zoom?: number;
}

export function Map({ stations, center = [-23.5505, -46.6333], onStationClick, zoom = 12 }: MapProps) {
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

  if (!isClient) return <div className="w-full h-screen bg-gray-200">Carregando mapa...</div>;

  return (
    <MapContainer center={center} zoom={zoom} className="w-full h-screen">
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; OpenStreetMap contributors'
      />

      {stations.map((station) => (
        <Marker
          key={station.id}
          position={[station.latitude, station.longitude]}
          eventHandlers={{
            click: () => onStationClick(station),
          }}
        >
          <Popup>
            <div className="text-sm font-semibold">{station.name}</div>
          </Popup>
        </Marker>
      ))}
    </MapContainer>
  );
}

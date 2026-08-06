'use client';

import { useEffect, useRef } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { GasStation } from '@/hooks/useGasStations';

interface MapProps {
  stations: GasStation[];
  center?: [number, number];
  onStationClick?: (station: GasStation) => void;
}

export function Map({ stations, center = [-23.5505, -46.6333], onStationClick }: MapProps) {
  const mapContainer = useRef<HTMLDivElement>(null);
  const map = useRef<L.Map | null>(null);
  const markersRef = useRef<{ [key: string]: L.Marker }>({});

  useEffect(() => {
    if (!mapContainer.current || map.current) return;

    map.current = L.map(mapContainer.current).setView(center, 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap',
      maxZoom: 19,
    }).addTo(map.current);

    return () => {
      if (map.current) {
        map.current.remove();
        map.current = null;
      }
    };
  }, [center]);

  useEffect(() => {
    if (!map.current) return;

    Object.values(markersRef.current).forEach(marker => marker.remove());
    markersRef.current = {};

    stations.forEach((station) => {
      const marker = L.marker([station.latitude, station.longitude], {
        title: station.name,
      }).addTo(map.current!);

      marker.bindPopup(`<div><strong>${station.name}</strong><br/>${station.address}</div>`);
      marker.on('click', () => onStationClick?.(station));

      markersRef.current[station.id] = marker;
    });

    if (stations.length > 0) {
      const group = new L.FeatureGroup(Object.values(markersRef.current));
      map.current.fitBounds(group.getBounds().pad(0.1));
    }
  }, [stations, onStationClick]);

  return <div ref={mapContainer} className="w-full h-full rounded-2xl" />;
}

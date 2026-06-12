import { fetchStations as fetchStationsApi } from "../components/api/mapApi"

export type ChargerType = "급속" | "완속"
export type StationStatus = "available" | "charging" | "unavailable"

export interface ChargingStation {
  id: string
  name: string
  address: string
  lat: number
  lng: number
  chargerType: ChargerType
  totalChargers: number
  availableChargers: number
  power: string
  operator: string
  status: StationStatus
  pricePerKwh: number
}

export const DUMMY_STATIONS: ChargingStation[] = [
  {
    id: "dg-001",
    name: "대구시청 공영주차장 충전소",
    address: "대구광역시 중구 공평로 88",
    lat: 35.8714,
    lng: 128.6014,
    chargerType: "급속",
    totalChargers: 4,
    availableChargers: 2,
    power: "100kW",
    operator: "한국전력",
    status: "available",
    pricePerKwh: 324,
  },
  {
    id: "dg-002",
    name: "동대구역 환승센터 충전소",
    address: "대구광역시 동구 동대구로 550",
    lat: 35.8797,
    lng: 128.6286,
    chargerType: "급속",
    totalChargers: 8,
    availableChargers: 0,
    power: "200kW",
    operator: "환경부",
    status: "charging",
    pricePerKwh: 347,
  },
  {
    id: "dg-003",
    name: "수성못 공영주차장 충전소",
    address: "대구광역시 수성구 용학로 36",
    lat: 35.8275,
    lng: 128.6212,
    chargerType: "완속",
    totalChargers: 6,
    availableChargers: 4,
    power: "7kW",
    operator: "GS차지비",
    status: "available",
    pricePerKwh: 292,
  },
  {
    id: "dg-004",
    name: "이월드 주차장 충전소",
    address: "대구광역시 달서구 두류공원로 200",
    lat: 35.8531,
    lng: 128.5653,
    chargerType: "급속",
    totalChargers: 3,
    availableChargers: 1,
    power: "50kW",
    operator: "차지비",
    status: "available",
    pricePerKwh: 313,
  },
  {
    id: "dg-005",
    name: "대구 스타디움 충전소",
    address: "대구광역시 수성구 유니버시아드로 180",
    lat: 35.8186,
    lng: 128.6786,
    chargerType: "완속",
    totalChargers: 10,
    availableChargers: 7,
    power: "7kW",
    operator: "한국전력",
    status: "available",
    pricePerKwh: 292,
  },
  {
    id: "dg-006",
    name: "칠곡 홈플러스 충전소",
    address: "대구광역시 북구 동천로 110",
    lat: 35.9442,
    lng: 128.5631,
    chargerType: "급속",
    totalChargers: 5,
    availableChargers: 3,
    power: "100kW",
    operator: "이브이시스",
    status: "available",
    pricePerKwh: 324,
  },
  {
    id: "dg-007",
    name: "성서산업단지 공영충전소",
    address: "대구광역시 달서구 성서공단로 213",
    lat: 35.8458,
    lng: 128.4889,
    chargerType: "급속",
    totalChargers: 6,
    availableChargers: 0,
    power: "200kW",
    operator: "환경부",
    status: "unavailable",
    pricePerKwh: 347,
  },
  {
    id: "dg-008",
    name: "반월당 지하상가 충전소",
    address: "대구광역시 중구 달구벌대로 2100",
    lat: 35.8651,
    lng: 128.5931,
    chargerType: "완속",
    totalChargers: 4,
    availableChargers: 2,
    power: "11kW",
    operator: "GS차지비",
    status: "charging",
    pricePerKwh: 292,
  },
  {
    id: "dg-009",
    name: "대구공항 주차타워 충전소",
    address: "대구광역시 동구 공항로 221",
    lat: 35.8983,
    lng: 128.659,
    chargerType: "급속",
    totalChargers: 7,
    availableChargers: 5,
    power: "100kW",
    operator: "한국전력",
    status: "available",
    pricePerKwh: 324,
  },
  {
    id: "dg-010",
    name: "두류네거리 공영주차장 충전소",
    address: "대구광역시 달서구 달구벌대로 1600",
    lat: 35.8521,
    lng: 128.5599,
    chargerType: "완속",
    totalChargers: 8,
    availableChargers: 6,
    power: "7kW",
    operator: "차지비",
    status: "available",
    pricePerKwh: 292,
  },
  {
    id: "dg-011",
    name: "경북대학교 북문 충전소",
    address: "대구광역시 북구 대학로 80",
    lat: 35.8902,
    lng: 128.6109,
    chargerType: "급속",
    totalChargers: 4,
    availableChargers: 2,
    power: "50kW",
    operator: "이브이시스",
    status: "available",
    pricePerKwh: 313,
  },
  {
    id: "dg-012",
    name: "앞산공원 주차장 충전소",
    address: "대구광역시 남구 앞산순환로 460",
    lat: 35.8197,
    lng: 128.5852,
    chargerType: "완속",
    totalChargers: 5,
    availableChargers: 3,
    power: "7kW",
    operator: "환경부",
    status: "available",
    pricePerKwh: 292,
  },
]

export async function fetchStations(): Promise<ChargingStation[]> {
  try {
    const data = await fetchStationsApi()

    if (!Array.isArray(data)) {
      throw new Error("Invalid stations payload")
    }

    const mapped = data.map((item: any) => ({
      id: String(item.id),
      name: String(item.name ?? ""),
      address: String(item.address ?? ""),
      lat: Number(item.lat),
      lng: Number(item.lng),
      chargerType: (item.chargerType as ChargerType) ?? "완속",
      totalChargers: Number(item.totalChargers ?? 0),
      availableChargers: Number(item.availableChargers ?? 0),
      power: String(item.power ?? ""),
      operator: String(item.operator ?? ""),
      status: (item.status as StationStatus) ?? "available",
      pricePerKwh: Number(item.pricePerKwh ?? 0),
    }))

    return mapped
  } catch (err) {
    console.error("fetchStations(): failed to load stations, using dummy data", err)
    return DUMMY_STATIONS
  }
}

export const DAEGU_CENTER = { lat: 35.8714, lng: 128.6014 }
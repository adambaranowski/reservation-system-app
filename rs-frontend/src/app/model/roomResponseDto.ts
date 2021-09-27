/**
 * RoomEquipment API
 * This API is used to provide availability to customize rooms and their equipment. <br><br> Only users with given authorities can use it: <br><ul> <li>ADMIN</li> <li>SUPER ADMIN</li> </ul>
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { EquipmentResponseDto } from './equipmentResponseDto';

/**
 * Response object of one room
 */
export interface RoomResponseDto { 
    /**
     * Unique number of room
     */
    roomNumber?: number;
    /**
     * Room description
     */
    description?: string;
    roomStatus?: RoomResponseDto.RoomStatusEnum;
    equipmentItems?: Array<EquipmentResponseDto>;
}
export namespace RoomResponseDto {
    export type RoomStatusEnum = 'NORMAL' | 'ONLY_TEACHER' | 'ONLY_PRINCIPAL';
    export const RoomStatusEnum = {
        NORMAL: 'NORMAL' as RoomStatusEnum,
        ONLYTEACHER: 'ONLY_TEACHER' as RoomStatusEnum,
        ONLYPRINCIPAL: 'ONLY_PRINCIPAL' as RoomStatusEnum
    };
}
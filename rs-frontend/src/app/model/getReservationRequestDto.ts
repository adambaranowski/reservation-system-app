/**
 * Reservation Api
 * Reservation Api
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

/**
 * Dto for requesting all reservations for room in given period of time
 */
export interface GetReservationRequestDto { 
    /**
     * Number of room for which you want to get reservations
     */
    roomNumber?: number;
    /**
     * Start date of period of time from which you want to get reservations. Must Be before endDate
     */
    beginDate?: string;
    /**
     * End date of period of time from which you want to get reservations. Must Be after startDate
     */
    endDate?: string;
}
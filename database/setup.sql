-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 03, 2025 at 05:22 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `banking_system`
--

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `user_type` varchar(20) NOT NULL,
  `balance` decimal(10,2) DEFAULT 0.00,
  `account_number` varchar(20) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `full_name`, `phone_number`, `user_type`, `balance`, `account_number`, `created_at`) VALUES
(1, 'admin', 'admin123', 'System Admin', '1234567890', 'admin', 0.00, NULL, '2025-05-03 03:05:25'),
(2, 'marwn', '9Y/cTCOOX1HSwYfPNBDoc90OcvtBYtob9W9q5czpUvo=', 'adsasd', 'asdasdas', 'customer', 0.00, 'ACC1746241546546', '2025-05-03 03:05:46'),
(3, 'salah', '9Y/cTCOOX1HSwYfPNBDoc90OcvtBYtob9W9q5czpUvo=', 'salah saleh', '1234', 'customer', 0.00, 'ACC1746241961010', '2025-05-03 03:12:41'),
(4, 'karem', '9Y/cTCOOX1HSwYfPNBDoc90OcvtBYtob9W9q5czpUvo=', 'karem karam', '213124132', 'customer', 0.00, 'ACC1746242447834', '2025-05-03 03:20:47');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

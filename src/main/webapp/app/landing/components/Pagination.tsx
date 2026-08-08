import React from 'react';
import { Pagination as BootstrapPagination } from 'react-bootstrap';

interface PaginationProps {
  activePage: number;
  itemsPerPage: number;
  totalItems: number;
  onPageChange: (page: number) => void;
  maxButtons?: number;
}

export const Pagination = ({ activePage, itemsPerPage, totalItems, onPageChange, maxButtons = 5 }: PaginationProps) => {
  if (totalItems <= itemsPerPage) {
    return null;
  }

  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const currentPage = Math.min(Math.max(activePage, 1), totalPages);

  const handleFirst = () => onPageChange(1);
  const handlePrev = () => onPageChange(Math.max(currentPage - 1, 1));
  const handleNext = () => onPageChange(Math.min(currentPage + 1, totalPages));
  const handleLast = () => onPageChange(totalPages);

  const halfWindow = Math.floor(maxButtons / 2);
  let startPage = Math.max(currentPage - halfWindow, 1);
  const endPage = Math.min(startPage + maxButtons - 1, totalPages);

  if (endPage - startPage + 1 < maxButtons) {
    startPage = Math.max(endPage - maxButtons + 1, 1);
  }

  const pageItems: number[] = [];
  for (let i = startPage; i <= endPage; i++) {
    pageItems.push(i);
  }

  return (
    <BootstrapPagination className="justify-content-center mt-4">
      <BootstrapPagination.First onClick={handleFirst} disabled={currentPage === 1} />
      <BootstrapPagination.Prev onClick={handlePrev} disabled={currentPage === 1} />
      {startPage > 1 && <BootstrapPagination.Ellipsis disabled />}
      {pageItems.map(page => (
        <BootstrapPagination.Item key={page} active={page === currentPage} onClick={() => onPageChange(page)}>
          {page}
        </BootstrapPagination.Item>
      ))}
      {endPage < totalPages && <BootstrapPagination.Ellipsis disabled />}
      <BootstrapPagination.Next onClick={handleNext} disabled={currentPage === totalPages} />
      <BootstrapPagination.Last onClick={handleLast} disabled={currentPage === totalPages} />
    </BootstrapPagination>
  );
};

export default Pagination;
